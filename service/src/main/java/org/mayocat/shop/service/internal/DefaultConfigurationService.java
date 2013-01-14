package org.mayocat.shop.service.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.configuration.Configuration;
import org.mayocat.shop.context.Execution;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.TenantConfiguration;
import org.mayocat.shop.service.ConfigurationService;
import org.mayocat.shop.service.NoSuchModuleException;
import org.mayocat.shop.store.TenantStore;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component
public class DefaultConfigurationService implements ConfigurationService
{
    @Inject
    private Provider<TenantStore> tenantStore;

    @Inject
    private Map<String, Configuration> configurations;

    @Inject
    private Execution execution;

    @Inject
    private Logger logger;

    @Override
    public Map<String, Object> getConfiguration()
    {
        Map<String, Object> tenantConfiguration = execution.getContext().getTenant().getConfiguration();
        Map<String, Object> platformConfiguration = this.mapFromConfigurations();
        ConfigurationMerger merger = new ConfigurationMerger(platformConfiguration, tenantConfiguration);
        return merger.merge();
    }

    @Override
    public Map<String, Object> getConfiguration(String module) throws NoSuchModuleException
    {
        if (!this.configurations.containsKey(module)) {
            throw new NoSuchModuleException();
        }

        try {
            return (Map<String, Object>) getConfiguration().get(module);
        }
        catch (ClassCastException e) {
            this.logger.warn("Attempt at accessing a configuration that is not an object");
        }
        return Collections.emptyMap();
    }

    @Override
    public void updateConfiguration(Map<String, Object> data)
    {
        ValidConfigurationEnforcer enforcer = new ValidConfigurationEnforcer(mapFromConfigurations(), data);
        ValidConfigurationEnforcer.ValidationResult result = enforcer.enforce();

        TenantConfiguration configuration =
                new TenantConfiguration(TenantConfiguration.CURRENT_VERSION, result.getResult());
        this.tenantStore.get().updateConfiguration(configuration);

        // TODO throw an exception here when there are validation errors, so that it can be acknowledged to the
        // REST service consumer ? (meaning the operation has been partially successful only)
    }

    @Override
    public void updateConfiguration(String module, Map<String, Object> configuration) throws NoSuchModuleException
    {
        if (!this.configurations.containsKey(module)) {
            throw new NoSuchModuleException();
        }

        Tenant tenant = this.execution.getContext().getTenant();
        TenantConfiguration currentConfiguration = tenant.getConfiguration();
        Map<String, Object> data = Maps.newHashMap(currentConfiguration.getData());

        data.put(module, configuration);
        this.updateConfiguration(data);
    }

    private Map<String, Object> mapFromConfigurations()
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(this.configurations);
            Map<String, Object> result = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
            return result;
        } catch (JsonProcessingException e) {
            this.logger.error("Failed to convert configurations to map", e);
        } catch (IOException e) {
            this.logger.error("Failed to convert configurations to map", e);
        }
        return Collections.emptyMap();
    }
}
