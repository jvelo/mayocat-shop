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
    class ComplexAddonValue
    {
        private Object raw;

        private Object display;

        ComplexAddonValue(Object raw, Object display)
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

    public Map<String, Object> build(Map<String, AddonGroup> definitions, List<Addon> addons)
    {
        Map<String, Object> context = Maps.newHashMap();
        for (String groupKey : definitions.keySet()) {

            AddonGroup group = definitions.get(groupKey);
            Map<String, Object> groupContext = Maps.newHashMap();

            for (String field : group.getFields().keySet()) {
                Optional<Addon> addon =
                        AddonContextBuilderHelper.findAddon(groupKey, field, addons);
                if (addon.isPresent()) {

                    AddonField addonField = group.getFields().get(field);
                    if (addonField.getProperties().containsKey("listValues") &&
                            AddonUtils.isListWithKeyAndDisplayValues(addonField))
                    {
                        List<Map<String, Object>> listValues =
                                (List<Map<String, Object>>) addonField.getProperties().get("listValues");
                        Object displayValue = null;
                        for (Map<String, Object> entry : listValues) {
                            if (entry.containsKey("key") &&
                                    entry.get("key").equals(addon.get().getValue().toString()))
                            {
                                displayValue = entry.get("name");
                                break;
                            }
                        }
                        groupContext.put(field, new ComplexAddonValue(
                                addon.get().getValue(),
                                displayValue == null ? addon.get().getValue() : displayValue
                        ));
                    } else {
                        groupContext.put(field, addonValue(addon.get().getValue()));
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
