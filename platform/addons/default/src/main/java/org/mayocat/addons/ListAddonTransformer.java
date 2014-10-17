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
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import static org.mayocat.addons.util.AddonUtils.emptyToNull;
import static org.mayocat.addons.util.AddonUtils.getListValues;

/**
 * @version $Id$
 */
@Component("list")
public class ListAddonTransformer implements AddonFieldTransformer
{
    @Override
    public Optional<AddonFieldValueWebObject> toWebView(EntityData<?> entityData,
            AddonFieldDefinition addonField, Object fieldValue)
    {
        List<String> values = (List<String>) fieldValue;
        List<String> displayValues = Lists.newArrayList();
        List<Map<String, Object>> listValues = getListValues(addonField);
        for (final String value : values) {
            Optional<Map<String, Object>> definition =
                    FluentIterable.from(listValues).filter(new Predicate<Map<String, Object>>()
                    {
                        public boolean apply(Map<String, Object> input)
                        {
                            return input.containsKey("key") && input.get("key").equals(value.toString());
                        }
                    }).first();
            displayValues
                    .add(definition.isPresent() && definition.get().containsKey("name") ?
                            (String) definition.get().get("name") : value);
        }
        return Optional.of(new AddonFieldValueWebObject(
                fieldValue,
                displayValues
        ));
    }
}
