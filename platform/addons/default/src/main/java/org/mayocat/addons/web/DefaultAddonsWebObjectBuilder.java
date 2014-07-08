/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.addons.AddonFieldTransformer;
import org.mayocat.addons.model.AddonFieldDefinition;
import org.mayocat.addons.model.AddonGroupDefinition;
import org.mayocat.addons.web.AddonFieldValueWebObject;
import org.mayocat.addons.web.AddonsWebObjectBuilder;
import org.mayocat.configuration.PlatformSettings;
import org.mayocat.context.WebContext;
import org.mayocat.entity.EntityData;
import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.model.AddonGroup;
import org.mayocat.model.HasAddons;
import org.mayocat.model.Localized;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component
public class DefaultAddonsWebObjectBuilder implements AddonsWebObjectBuilder
{
    @Inject
    private WebContext context;

    @Inject
    private PlatformSettings platformSettings;

    @Inject
    private Map<String, AddonFieldTransformer> fieldTransformers;

    @Inject
    private EntityLocalizationService entityLocalizationService;

    public Map<String, Object> build(EntityData<? extends HasAddons> entityData)
    {
        Map<String, Object> result = Maps.newHashMap();

        HasAddons entity;
        if (Localized.class.isAssignableFrom(entityData.getEntity().getClass())) {
            entity = (HasAddons) entityLocalizationService.localize((Localized) entityData.getEntity());
        } else {
            entity = entityData.getEntity();
        }

        if (!entity.getAddons().isLoaded()) {
            return Collections.emptyMap();
        }
        Map<String, AddonGroup> addons = entity.getAddons().get();

        for (String groupName : addons.keySet()) {

            Optional<AddonGroupDefinition> groupDefinition = findGroupDefinition(groupName);
            if (!groupDefinition.isPresent()) {
                // Just plain ignore addons groups/fields for which we can't find a definition
                continue;
            }

            AddonGroup group = addons.get(groupName);
            Map<String, Map<String, Object>> model = group.getModel();
            Object value = group.getValue();
            Object resultValue = null;

            if (List.class.isAssignableFrom(value.getClass())) {
                // Sequenced addons
                List<Map<String, Object>> valueAsList = (List<Map<String, Object>>) value;
                resultValue = new ArrayList<>();
                for (Map<String, Object> element : valueAsList) {
                    ((List) resultValue).add(getFieldsValue(entityData, groupDefinition.get(), model, element));
                }
            } else {
                // Not sequenced
                resultValue = getFieldsValue(entityData, groupDefinition.get(), model, (Map<String, Object>) value);
            }
            result.put(groupName, resultValue);
        }
        return result;
    }

    public Map<String, AddonFieldValueWebObject> getFieldsValue(EntityData entityData,
            AddonGroupDefinition groupDefinition, Map<String, Map<String, Object>> model, Map<String, Object> element)
    {
        Map<String, AddonFieldValueWebObject> fieldsResult = Maps.newHashMap();
        for (String fieldName : element.keySet()) {
            if (!model.containsKey(fieldName) || !groupDefinition.getFields().containsKey(fieldName)) {
                // Ignore fields for which we don't have a model or a field definition
                continue;
            }

            Map<String, Object> fieldModel = model.get(fieldName);
            if (!fieldModel.containsKey("type")) {
                continue;
            }

            String type = (String) fieldModel.get("type");
            AddonFieldDefinition fieldDefinition = groupDefinition.getFields().get(fieldName);
            Object value = element.get(fieldName);

            if (fieldTransformers.containsKey(type)) {
                AddonFieldTransformer transformer = fieldTransformers.get(type);

                Optional<AddonFieldValueWebObject> result =
                        transformer.toWebView(entityData, fieldDefinition, value);
                if (result.isPresent()) {
                    fieldsResult.put(fieldName, result.get());
                }
            }
            if (!fieldsResult.containsKey(fieldName)) {
                fieldsResult.put(fieldName, new AddonFieldValueWebObject(value, value));
            }
        }
        return fieldsResult;
    }

    private Optional<AddonGroupDefinition> findGroupDefinition(String groupName)
    {
        // First, find it in platform (has priority)

        if (platformSettings.getAddons().containsKey(groupName)) {
            return Optional.of(platformSettings.getAddons().get(groupName));
        }

        // Then, in theme
        if (context.getTheme().getDefinition() != null &&
                context.getTheme().getDefinition().getAddons().containsKey(groupName))
        {
            return Optional.of(context.getTheme().getDefinition().getAddons().get(groupName));
        }

        return Optional.absent();
    }
}
