/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class ConfigurationJsonMerger extends AbstractJsonConfigurationHandler
{
    public ConfigurationJsonMerger(final Map<String, Serializable> platform, final Map<String, Serializable> tenant)
    {
        super(platform, tenant);
    }

    public Map<String, Serializable> merge()
    {
        return SerializationUtils.clone(merge(platform, tenant));
    }

    private HashMap<String, Serializable> merge(@NotNull final Map<String, Serializable> global,
            @Nullable final Map<String, Serializable> local)
    {
        // Copy the global object
        HashMap<String, Serializable> result = Maps.newHashMap(global);

        // Iterate over all configuration keys, and merge with tenant configuration
        for (String key : result.keySet()) {
            Object value = global.get(key);
            if (value != null) {
                try {
                    Map<String, Serializable> valueAsMap = (Map<String, Serializable>) value;
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
                        Map<String, Serializable> localSubMap = null;
                        if (local != null && local.get(key) != null) {
                            try {
                                localSubMap = (Map<String, Serializable>) local.get(key);
                            } catch (ClassCastException e) {
                            }
                        }
                        HashMap<String, Serializable> mergedValue = this.merge(valueAsMap, localSubMap);
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
