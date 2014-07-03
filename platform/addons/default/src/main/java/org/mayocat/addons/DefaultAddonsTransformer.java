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

import javax.inject.Inject;

import org.mayocat.entity.EntityData;
import org.mayocat.model.AddonGroup;
import org.mayocat.model.HasAddons;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
@Component
public class DefaultAddonsTransformer implements AddonsTransformer
{
    interface TransformOperation
    {
        Optional<Object> apply(AddonFieldTransformer transformer, Object value);
    }

    @Inject
    private Map<String, AddonFieldTransformer> fieldTransformers;

    public void fromApi(final EntityData<? extends HasAddons> entityData)
    {
        transformAddons(entityData, new TransformOperation()
        {
            public Optional<Object> apply(AddonFieldTransformer transformer, Object value)
            {
                return transformer.fromApi(entityData, value);
            }
        });
    }

    public void toApi(final EntityData<? extends HasAddons> entityData)
    {
        transformAddons(entityData, new TransformOperation()
        {
            public Optional<Object> apply(AddonFieldTransformer transformer, Object value)
            {
                return transformer.toApi(entityData, value);
            }
        });
    }

    public void toWebView(final EntityData<? extends HasAddons> entityData)
    {
        transformAddons(entityData, new TransformOperation()
        {
            public Optional<Object> apply(AddonFieldTransformer transformer, Object value)
            {
                return transformer.toWebView(entityData, value);
            }
        });
    }

    private void transformAddons(EntityData<? extends HasAddons> entityData,
            TransformOperation operation)
    {
        HasAddons entity = entityData.getEntity();
        if (!entity.getAddons().isLoaded()) {
            throw new RuntimeException("Cannot transform addons not loaded");
        }
        Map<String, AddonGroup> addons = entity.getAddons().get();

        for (AddonGroup group : addons.values()) {
            Map<String, Map<String, Object>> model = group.getModel();
            Object value = group.getValue();

            if (List.class.isAssignableFrom(value.getClass())) {
                // Sequenced addons
                List<Map<String, Object>> valueAsList = (List<Map<String, Object>>) value;
                for (Map<String, Object> element : valueAsList) {
                    transformFields(operation, model, element);
                }
            } else {
                // Not sequenced
                transformFields(operation, model, (Map<String, Object>) value);
            }
        }
    }

    private void transformFields(TransformOperation operation,
            Map<String, Map<String, Object>> model, Map<String, Object> element)
    {
        for (String fieldName : element.keySet()) {
            if (!model.containsKey(fieldName)) {
                continue;
            }

            Map<String, Object> fieldModel = model.get(fieldName);
            if (!fieldModel.containsKey("type")) {
                continue;
            }

            String type = (String) fieldModel.get("type");

            if (fieldTransformers.containsKey(type)) {
                AddonFieldTransformer transformer = fieldTransformers.get(type);

                Optional<Object> result = operation.apply(transformer, element.get(fieldName));
                if (result.isPresent()) {
                    element.put(fieldName, result.get());
                }
            }
        }
    }
}
