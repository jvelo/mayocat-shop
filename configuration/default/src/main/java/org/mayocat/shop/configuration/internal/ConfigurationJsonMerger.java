package org.mayocat.shop.configuration.internal;

import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class ConfigurationJsonMerger extends AbstractJsonConfigurationHandler
{
    public ConfigurationJsonMerger(final Map<String, Object> platform, final Map<String, Object> tenant)
    {
        super(platform, tenant);
    }

    public Map<String, Object> merge()
    {
        return merge(platform, tenant);
    }

    private Map<String, Object> merge(@NotNull final Map<String, Object> global,
            @Nullable final Map<String, Object> local)
    {
        // Copy the global object
        Map<String, Object> result = Maps.newHashMap(global);
        for (String key : result.keySet()) {
            Object value = global.get(key);
            if (value != null) {
                try {
                    Map<String, Object> valueAsMap = (Map<String, Object>) value;
                    if (isConfigurableEntry(valueAsMap)) {
                        // We have a configurable
                        Boolean isConfigurable = (Boolean) valueAsMap.get(CONFIGURABLE_KEY);
                        if (isConfigurable && local != null && local.containsKey(key)) {
                            valueAsMap.put(VALUE_KEY, local.get(key));
                        } else {
                            // Either their is no local configuration override, or overriding
                            // is not permitted for this entry : we set the default value as value
                            valueAsMap.put(VALUE_KEY, valueAsMap.get(DEFAULT_KEY));
                        }
                    } else {
                        // We need to go deeper
                        Map<String, Object> localSubMap = null;
                        if (local != null && local.get(key) != null) {
                            try {
                                localSubMap = (Map<String, Object>) local.get(key);
                            } catch (ClassCastException e) {
                            }
                        }
                        Object mergedValue = this.merge(valueAsMap, localSubMap);
                        result.put(key, mergedValue);
                    }
                } catch (ClassCastException e) {
                    // This mean the value is not a map : we are likely on a leaf of the configuration tree
                    // (or it is a list, see below)
                }
                // TODO add support for list of values
                // i.e. try to cast to List<Object> etc.
            }
        }
        return result;
    }
}
