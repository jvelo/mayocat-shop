/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mayocat.addons.model.AddonFieldDefinition;
import org.mayocat.addons.model.AddonGroupDefinition;
import org.mayocat.model.AddonGroup;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class AddonUtils
{
    public static Map<String, AddonGroup> asMap(List<AddonGroup> addons)
    {
        Map<String, AddonGroup> addonsAsMap = new HashMap<>();
        for (AddonGroup addonGroup : addons) {
            addonsAsMap.put(addonGroup.getGroup(), addonGroup);
        }
        return addonsAsMap;
    }

    /**
     * Finds a addon group definition in a list of group definitions. The priority of the group definitions is order in
     * which they are passed : first passed has the highest priority, last passed the lowest.
     *
     * @param name the name of the addon group definition to find
     * @param groupDefinitions the list of group definitions
     * @return the first definition found, or absent if none is found
     */
    public static Optional<AddonGroupDefinition> findAddonGroupDefinition(String name,
            Map<String, AddonGroupDefinition>... groupDefinitions)
    {
        for (Map<String, AddonGroupDefinition> groupDefinition : groupDefinitions) {
            if (groupDefinition.containsKey(name)) {
                return Optional.of(groupDefinition.get(name));
            }
        }
        return Optional.absent();
    }

    public static boolean isListWithKeyAndDisplayValues(AddonFieldDefinition field)
    {
        return isListWithKeyAndDisplayValues(field.getProperties());
    }

    public static boolean isListWithKeyAndDisplayValues(Map<String, Object> properties)
    {
        if (properties == null) {
            return false;
        }
        Object listValues = properties.get("list.values");
        if (listValues == null) {
            // For backward compatibility
            listValues = properties.get("listValues");
        }
        if (listValues == null) {
            return false;
        }
        try {
            List<?> values = (List) listValues;
            if (values.size() <= 0) {
                return false;
            }
            Map<String, Object> casted = (Map<String, Object>) values.get(0);
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }

    public static List<Map<String, Object>> getListValues(AddonFieldDefinition fieldDefinition)
    {
        if (fieldDefinition.getProperties().containsKey("list.values")) {
            return (List<Map<String, Object>>) fieldDefinition.getProperties().get("list.values");
        }
        // Backward compatibility
        return (List<Map<String, Object>>) fieldDefinition.getProperties().get("listValues");
    }
}
