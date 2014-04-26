/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.internal;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.TenantConfiguration;
import org.mayocat.accounts.store.TenantStore;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.ExposedSettings;
import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.configuration.NoSuchModuleException;
import org.mayocat.configuration.jackson.GestaltConfigurationModule;
import org.mayocat.context.WebContext;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.yammer.dropwizard.json.ObjectMapperFactory;

/**
 * @version $Id$
 */
@Component
public class DefaultConfigurationService implements ConfigurationService
{
    @Inject
    private Provider<TenantStore> tenantStore;

    @Inject
    private Map<String, ExposedSettings> exposedSettings;

    @Inject
    private Map<String, GestaltConfigurationSource> gestaltConfigurationSources;

    @Inject
    private ObjectMapperFactory objectMapperFactory;

    @Inject
    private WebContext context;

    @Inject
    private Logger logger;

    /**
     * Configurations cache.
     *
     * Keys are tenant ids, and values their configuration as JSON (here a map).
     *
     * An event listener flushed their entry when a tenant entity event is received.
     */
    private Cache<UUID, Map<String, Serializable>> configurations = CacheBuilder.newBuilder().maximumSize(1000).build();

    private Object lock = new Object();

    /**
     * Exposed settings, as provided by the platform
     */
    private Map<String, Serializable> exposedPlatformSettingsAsJson;

    public Map<Class, Serializable> getSettings()
    {
        return this.getSettings(this.context.getTenant());
    }

    public Map<Class, Serializable> getSettings(Tenant tenant)
    {
        ObjectMapper mapper = getObjectMapper();
        Map<String, Serializable> mergedConfiguration = getSettingsAsJson(tenant);
        Map<Class, Serializable> configurations = Maps.newHashMap();
        for (String source : exposedSettings.keySet()) {
            Map<String, Serializable> merged = (Map<String, Serializable>) mergedConfiguration.get(source);
            Class c = exposedSettings.get(source).getClass();
            try {
                String json = mapper.writeValueAsString(merged);
                Serializable result = (Serializable) mapper.readValue(json, c);
                configurations.put(c, result);
            } catch (JsonProcessingException e) {
                this.logger.error("Error while converting configuration to JSON string", e);
            } catch (IOException e) {
                this.logger.error("Error while converting configuration to JSON string", e);
            }
        }
        return configurations;
    }

    public <T extends ExposedSettings> T getSettings(Class<T> c, Tenant tenant)
    {
        return (T) this.getSettings(tenant).get(c);
    }

    public <T extends ExposedSettings> T getSettings(Class<T> c)
    {
        return (T) this.getSettings().get(c);
    }

    public Map<String, Serializable> getSettingsAsJson(final Tenant tenant)
    {
        if (tenant == null) {
            return Collections.emptyMap();
        }
        try {
            return configurations.get(tenant.getId(), new Callable<Map<String, Serializable>>()
            {
                @Override
                public Map<String, Serializable> call()
                {
                    synchronized (lock) {
                        logger.debug("loading cache configuration value for tenant {}", tenant.getSlug());
                        Map<String, Serializable> tenantConfiguration = tenant.getConfiguration();
                        Map<String, Serializable> platformConfiguration = getExposedPlatformSettingsAsJson();
                        ConfigurationJsonMerger merger =
                                new ConfigurationJsonMerger(platformConfiguration, tenantConfiguration);
                        return merger.merge();
                    }
                }
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Serializable> getSettingsAsJson()
    {
        return this.getSettingsAsJson(context.getTenant());
    }

    public Map<String, Serializable> getSettingsAsJson(String moduleName) throws NoSuchModuleException
    {
        if (!this.exposedSettings.containsKey(moduleName)) {
            throw new NoSuchModuleException();
        }

        try {
            return (Map<String, Serializable>) getSettingsAsJson().get(moduleName);
        } catch (ClassCastException e) {
            this.logger.warn("Attempt at accessing a configuration that is not an object");
        }
        return Collections.emptyMap();
    }

    public void updateSettings(Map<String, Serializable> data)
    {
        ValidConfigurationEnforcer enforcer = new ValidConfigurationEnforcer(getExposedPlatformSettingsAsJson(), data);
        ValidConfigurationEnforcer.ValidationResult result = enforcer.enforce();

        TenantConfiguration configuration =
                new TenantConfiguration(TenantConfiguration.CURRENT_VERSION, result.getResult());
        this.tenantStore.get().updateConfiguration(configuration);

        // Invalidates the cached configuration for the tenant updating its configuration
        // TODO: do this from the configuration store instead
        this.configurations.invalidate(this.context.getTenant().getId());

        // TODO throw an exception here when there are validation errors, so that it can be acknowledged to the
        // REST accounts consumer ? (meaning the operation has been partially successful only)
    }

    public void updateSettings(String module, Map<String, Serializable> configuration) throws NoSuchModuleException
    {
        if (!this.exposedSettings.containsKey(module)) {
            throw new NoSuchModuleException();
        }

        Tenant tenant = this.context.getTenant();
        TenantConfiguration currentConfiguration = tenant.getConfiguration();
        Map<String, Serializable> data = Maps.newHashMap(currentConfiguration.getData());

        data.put(module, Maps.newHashMap(configuration));
        this.updateSettings(data);
    }

    public Map<String, Serializable> getGestaltConfiguration()
    {
        ObjectMapper mapper = getObjectMapper();
        mapper.registerModule(new GestaltConfigurationModule());

        Map<String, Object> result = Maps.newHashMap();
        for (String key : this.gestaltConfigurationSources.keySet()) {
            Object value = this.gestaltConfigurationSources.get(key).get();
            if (ExposedSettings.class.isAssignableFrom(value.getClass())) {
                // If the gestalt source returns an "exposed setting", then we get the version merged with the tenant
                // configuration.
                Class<? extends ExposedSettings> configurationClass =
                        (Class<? extends ExposedSettings>) value.getClass();
                value = this.getSettings(configurationClass);
            }
            result.put(key, value);
        }
        try {
            return mapper.readValue(mapper.writeValueAsString(result), new TypeReference<Map<String, Object>>()
            {
            });
        } catch (JsonProcessingException e) {
            this.logger.error("Failed to convert gestalt configuration [{}]", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            this.logger.error("Failed to convert configurations to map", e);
            throw new RuntimeException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Map<String, Serializable> getExposedPlatformSettingsAsJson()
    {
        if (exposedPlatformSettingsAsJson != null) {
            return exposedPlatformSettingsAsJson;
        }
        ObjectMapper mapper = getObjectMapper();
        Map<String, ExposedSettings> configurationsToSerialize = Maps.newHashMap();
        for (String hint : exposedSettings.keySet()) {
            configurationsToSerialize.put(hint, exposedSettings.get(hint));
        }
        try {
            String json = mapper.writeValueAsString(configurationsToSerialize);
            exposedPlatformSettingsAsJson = mapper.readValue(json, new TypeReference<Map<String, Object>>()
            {
            });
            return exposedPlatformSettingsAsJson;
        } catch (JsonProcessingException e) {
            this.logger.error("Failed to convert configurations to map", e);
        } catch (IOException e) {
            this.logger.error("Failed to convert configurations to map", e);
        }
        return Collections.emptyMap();
    }

    private ObjectMapper getObjectMapper()
    {
        return objectMapperFactory.build();
    }
}