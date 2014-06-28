/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons.front.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mayocat.addons.model.AddonFieldDefinition;
import org.mayocat.addons.model.AddonGroupDefinition;
import org.mayocat.addons.util.AddonUtils;
import org.mayocat.model.AddonGroup;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class AddonContextBuilder
{
    class AddonValue
    {
        private Object raw;

        private Object display;

        AddonValue(Object raw, Object display)
        {
            this.raw = raw;
            this.display = display;
        }

        public Object getRaw()
        {
            return raw;
        }

        public Object getDisplay()
        {
            return display;
        }
    }

    public Map<String, Object> build(Map<String, AddonGroupDefinition> definitions, Map<String, AddonGroup> addons)
    {
        return build(definitions, addons, "theme");
    }

    public Map<String, Object> build(Map<String, AddonGroupDefinition> definitions, Map<String, AddonGroup> addons,
            String source)
    {
        Map<String, Object> context = Maps.newHashMap();
        for (String groupKey : definitions.keySet()) {

            AddonGroupDefinition group = definitions.get(groupKey);
            Object groupContext = null;

            if (addons.containsKey(groupKey)) {
                AddonGroup addonGroup = addons.get(groupKey);

                Map<String, Object> model = addonGroup.getModel();
                Object value = addonGroup.getValue();

                if (List.class.isAssignableFrom(value.getClass())) {
                    List<Map<String, Object>> listContext = new ArrayList<>();
                    List<Map<String, Object>> sequence = (List<Map<String, Object>>) value;
                    for (Map<String, Object> itemInSequence : sequence) {
                        listContext.add(buildGroupItemContext(group, model, itemInSequence));
                    }

                    groupContext = listContext;
                } else {
                    groupContext = buildGroupItemContext(group, model, (Map<String, Object>) value);
                }
            }

            if (groupContext != null) {
                context.put(groupKey, groupContext);
            }
        }
        return context;
    }

    private Map<String, Object> buildGroupItemContext(AddonGroupDefinition group, Map<String, Object> model,
            Map<String, Object> value)
    {
        Map<String, Object> valueMap = value;
        Map<String, Object> groupContext = Maps.newHashMap();

        for (String key : group.getFields().keySet()) {
            AddonFieldDefinition addonField = group.getFields().get(key);
            if (model.containsKey(key) && valueMap.containsKey(key)) {

                Object fieldValue = valueMap.get(key);

                if (addonField.getProperties().containsKey("listValues") &&
                        AddonUtils.isListWithKeyAndDisplayValues(addonField))
                {
                    List<Map<String, Object>> listValues =
                            (List<Map<String, Object>>) addonField.getProperties().get("listValues");
                    Object displayValue = null;
                    for (Map<String, Object> entry : listValues) {
                        if (entry.containsKey("key") && fieldValue != null &&
                                entry.get("key").equals(fieldValue.toString()))
                        {
                            displayValue = entry.get("name");
                            break;
                        }
                    }
                    groupContext.put(key, new AddonValue(
                            fieldValue,
                            displayValue == null ? addonValue(fieldValue) : displayValue
                    ));
                } else {
                    groupContext.put(key, new AddonValue(
                            addonValue(fieldValue),
                            addonValue(fieldValue)
                    ));
                }
            }
        }

        return groupContext;
    }

    private static Object addonValue(Object value)
    {
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(value.getClass())) {
            return Strings.emptyToNull((String) value);
        }
        return value;
    }
}
