/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons.front.builder;

import java.util.List;
import java.util.Map;

import org.mayocat.addons.model.AddonField;
import org.mayocat.addons.model.AddonGroup;
import org.mayocat.addons.util.AddonUtils;
import org.mayocat.model.Addon;

import com.google.common.base.Optional;
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

    public Map<String, Object> build(Map<String, AddonGroup> definitions, List<Addon> addons) {
        return build(definitions, addons, "theme");
    }

    public Map<String, Object> build(Map<String, AddonGroup> definitions, List<Addon> addons, String source)
    {
        Map<String, Object> context = Maps.newHashMap();
        for (String groupKey : definitions.keySet()) {

            AddonGroup group = definitions.get(groupKey);
            Map<String, Object> groupContext = Maps.newHashMap();

            for (String field : group.getFields().keySet()) {
                Optional<Addon> addon =
                        AddonContextBuilderHelper.findAddon(groupKey, field, addons, source);
                if (addon.isPresent()) {

                    AddonField addonField = group.getFields().get(field);
                    if (addonField.getProperties().containsKey("listValues") &&
                            AddonUtils.isListWithKeyAndDisplayValues(addonField))
                    {
                        List<Map<String, Object>> listValues =
                                (List<Map<String, Object>>) addonField.getProperties().get("listValues");
                        Object displayValue = null;
                        for (Map<String, Object> entry : listValues) {
                            if (entry.containsKey("key") && addon.get().getValue() != null &&
                                    entry.get("key").equals(addon.get().getValue().toString()))
                            {
                                displayValue = entry.get("name");
                                break;
                            }
                        }
                        groupContext.put(field, new AddonValue(
                                addon.get().getValue(),
                                displayValue == null ? addonValue(addon.get().getValue()) : displayValue
                        ));
                    } else {
                        groupContext.put(field, new AddonValue(
                                addonValue(addon.get().getValue()),
                                addonValue(addon.get().getValue())
                        ));
                    }
                } else {
                    groupContext.put(field, null);
                }
            }

            context.put(groupKey, groupContext);
        }
        return context;
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
