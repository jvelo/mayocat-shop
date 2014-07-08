/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons;

import java.util.List;
import java.util.Map;

import org.mayocat.addons.model.AddonFieldDefinition;
import org.mayocat.addons.web.AddonFieldValueWebObject;
import org.mayocat.entity.EntityData;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

import static org.mayocat.addons.util.AddonUtils.getListValues;
import static org.mayocat.addons.util.AddonUtils.isListWithKeyAndDisplayValues;

/**
 * @version $Id$
 */
@Component("string")
public class StringAddonTransformer implements AddonFieldTransformer
{
    public Optional<AddonFieldValueWebObject> toWebView(EntityData<?> entityData,
            AddonFieldDefinition addonField, Object fieldValue)
    {
        if (isListWithKeyAndDisplayValues(addonField))
        {
            List<Map<String, Object>> listValues = getListValues(addonField);
            Object displayValue = null;
            for (Map<String, Object> entry : listValues) {
                if (entry.containsKey("key") && fieldValue != null &&
                        entry.get("key").equals(fieldValue.toString()))
                {
                    displayValue = entry.get("name");
                    break;
                }
            }
            return Optional.of(new AddonFieldValueWebObject(
                    fieldValue,
                    displayValue == null ? emptyToNull(fieldValue) : displayValue
            ));
        } else {
            return Optional.of(new AddonFieldValueWebObject(
                    emptyToNull(fieldValue),
                    emptyToNull(fieldValue)
            ));
        }
    }

    private static Object emptyToNull(Object value)
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
