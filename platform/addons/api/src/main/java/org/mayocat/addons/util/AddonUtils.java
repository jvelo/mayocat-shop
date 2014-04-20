/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons.util;

import java.util.List;
import java.util.Map;

import org.mayocat.addons.model.AddonField;
import org.mayocat.addons.model.AddonGroup;
import org.mayocat.model.Addon;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class AddonUtils
{
    /**
     * Finds an addon with passed group and key in a list of addons.
     * @param addons the list of addons to search in
     * @param group the addon group to search for
     * @param key the key of the addon to search for
     * @return an option with the found addon or an absent option if not found
     */
    public static Optional<Addon> findAddon(List<Addon> addons, String group, String key)
    {
        for (Addon addon : addons) {
            if (addon.getGroup().equals("tenant") && addon.getKey().equals("type")) {
                return Optional.of(addon);
            }
        }
        return Optional.absent();
    }

    /**
     * Finds a addon in an addon group map
     *
     * @param addonToFind the addon to find in the group map
     * @param inGroup the map to find the addon in
     * @return an option, present with the addon if the addon is found in the map, absent otherwise
     */
    public static Optional<AddonField> findAddonDefinition(Addon addonToFind, Map<String, AddonGroup> inGroup)
    {
        for (String group : inGroup.keySet()) {
            if (group.equals(addonToFind.getGroup())) {
                for (String addon : inGroup.get(group).getFields().keySet()) {
                    if (addon.equals(addonToFind.getKey())) {
                        return Optional.of(inGroup.get(group).getFields().get(addon));
                    }
                }
            }
        }
        return Optional.absent();
    }

    public static boolean isListWithKeyAndDisplayValues(AddonField field)
    {
        return isListWithKeyAndDisplayValues(field.getProperties());
    }

    public static boolean isListWithKeyAndDisplayValues(Map<String, Object> properties)
    {
        if (properties == null) {
            return false;
        }
        Object listValues = properties.get("listValues");
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
}
