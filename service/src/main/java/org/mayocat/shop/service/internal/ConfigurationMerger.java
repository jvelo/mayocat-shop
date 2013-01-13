package org.mayocat.shop.service.internal;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class ConfigurationMerger
{
    public static final String CONFIGURABLE_KEY = "configurable";

    public static final String DEFAULT_KEY = "default";

    public static final String VISIBLE_KEY = "visible";

    public static final String VALUE_KEY = "value";

    private final Map<String, Object> platform;

    private final Map<String, Object> tenant;

    public ConfigurationMerger(final Map<String, Object> platform, final Map<String, Object> tenant)
    {
        this.platform = platform;
        this.tenant = tenant;
    }

    public Map<String, Object> merge()
    {
        return merge(platform, tenant);
    }

    private Map<String, Object> merge(final Map<String, Object> global, final Map<String, Object> local)
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
                        //result.put(key, valueAsMap);
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

    private boolean isConfigurableEntry(Map<String, Object> entry)
    {
        return entry.containsKey(CONFIGURABLE_KEY)
            && Boolean.class.isAssignableFrom(entry.get(CONFIGURABLE_KEY).getClass())
            && entry.containsKey(DEFAULT_KEY)
            && entry.containsKey(VISIBLE_KEY);
    }

}
