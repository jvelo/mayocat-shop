package org.mayocat.configuration.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.configuration.ConfigurationSource;
import org.mayocat.context.Execution;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.TenantConfiguration;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.NoSuchModuleException;
import org.mayocat.accounts.store.TenantStore;
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
    private Map<String, ConfigurationSource> configurationSources;

    @Inject
    private Execution execution;

    @Inject
    private Logger logger;

    @Override
    public Map<Class, Object> getConfigurations()
    {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mergedConfiguration = getConfigurationAsJson();
        Map<Class, Object> configurations = Maps.newHashMap();
        for (String source : configurationSources.keySet()) {
            Map<String, Object> merged = (Map<String, Object>) mergedConfiguration.get(source);
            Class c = configurationSources.get(source).get().getClass();
            try {
                String json = mapper.writeValueAsString(merged);
                Object result = mapper.readValue(json, c);
                configurations.put(c, result);
            } catch (JsonProcessingException e) {
                this.logger.error("Error while converting configuration to JSON string", e);
            } catch (IOException e) {
                this.logger.error("Error while converting configuration to JSON string", e);
            }
        }
        return configurations;
    }

    @Override
    public Object getConfiguration(Class c)
    {
        return this.getConfigurations().get(c);
    }

    @Override
    public Map<String, Object> getConfigurationAsJson()
    {
        Map<String, Object> tenantConfiguration = execution.getContext().getTenant().getConfiguration();
        Map<String, Object> platformConfiguration = this.readConfigurationsAsJson();
        ConfigurationJsonMerger merger = new ConfigurationJsonMerger(platformConfiguration, tenantConfiguration);
        return merger.merge();
    }

    @Override
    public Map<String, Object> getConfigurationAsJson(String moduleName) throws NoSuchModuleException
    {
        if (!this.configurationSources.containsKey(moduleName)) {
            throw new NoSuchModuleException();
        }

        try {
            return (Map<String, Object>) getConfigurationAsJson().get(moduleName);
        } catch (ClassCastException e) {
            this.logger.warn("Attempt at accessing a configuration that is not an object");
        }
        return Collections.emptyMap();
    }

    @Override
    public void updateConfiguration(Map<String, Object> data)
    {
        ValidConfigurationEnforcer enforcer = new ValidConfigurationEnforcer(readConfigurationsAsJson(), data);
        ValidConfigurationEnforcer.ValidationResult result = enforcer.enforce();

        TenantConfiguration configuration =
                new TenantConfiguration(TenantConfiguration.CURRENT_VERSION, result.getResult());
        this.tenantStore.get().updateConfiguration(configuration);

        // TODO throw an exception here when there are validation errors, so that it can be acknowledged to the
        // REST accounts consumer ? (meaning the operation has been partially successful only)
    }

    @Override
    public void updateConfiguration(String module, Map<String, Object> configuration) throws NoSuchModuleException
    {
        if (!this.configurationSources.containsKey(module)) {
            throw new NoSuchModuleException();
        }

        Tenant tenant = this.execution.getContext().getTenant();
        TenantConfiguration currentConfiguration = tenant.getConfiguration();
        Map<String, Object> data = Maps.newHashMap(currentConfiguration.getData());

        data.put(module, configuration);
        this.updateConfiguration(data);
    }

    private Map<String, Object> readConfigurationsAsJson()
    {
        ObjectMapper mapper = new ObjectMapper();
        Map configurationsToSerialize = Maps.newHashMap();
        for (String hint : configurationSources.keySet()) {
            ConfigurationSource c = configurationSources.get(hint);
            configurationsToSerialize.put(hint, c.get());
        }
        try {
            String json = mapper.writeValueAsString(configurationsToSerialize);
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
