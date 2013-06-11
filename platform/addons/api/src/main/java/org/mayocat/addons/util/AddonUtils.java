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
            if (((List) listValues).size() <= 0) {
                return false;
            }
            Map<String, Object> casted = (Map<String, Object>) values.get(0);
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }
}
