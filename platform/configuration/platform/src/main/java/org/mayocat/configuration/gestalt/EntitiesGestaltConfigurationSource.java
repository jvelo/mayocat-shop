/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.gestalt;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.addons.model.AddonGroupDefinition;
import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.configuration.PlatformSettings;
import org.mayocat.configuration.images.ImageFormatDefinition;
import org.mayocat.context.WebContext;
import org.mayocat.meta.EntityMeta;
import org.mayocat.meta.EntityMetaRegistry;
import org.mayocat.model.AddonSource;
import org.mayocat.theme.Model;
import org.mayocat.theme.Theme;
import org.mayocat.theme.TypeDefinition;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component("entities")
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class EntitiesGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private PlatformSettings platformSettings;

    @Inject
    private EntityMetaRegistry entityMetaRegistry;

    @Inject
    private WebContext context;

    @Override
    public Object get()
    {
        Map<String, Map<String, Object>> entities = Maps.newLinkedHashMap();
        for (EntityMeta meta : entityMetaRegistry.getEntities()) {
            Map data = Maps.newHashMap();
            entities.put(meta.getEntityName(), data);
        }

        Theme theme = context.getTheme();

        if (theme != null) {
            addAddons(entities, theme.getDefinition().getAddons(), AddonSource.THEME);
            addModels(entities, theme.getDefinition().getModels());
            addImageFormats(entities, theme.getDefinition().getImageFormats());
            addTypes(entities, theme.getDefinition().getProductTypes());
        }

        addAddons(entities, platformSettings.getAddons(), AddonSource.PLATFORM);

        return entities;
    }

    private void addTypes(Map<String, Map<String, Object>> entities, Map<String, TypeDefinition> productTypes)
    {
        // Right now we support only products, but ultimately, other entities could have types too.
        entities.get("product").put("types", productTypes);
    }

    private void addImageFormats(Map<String, Map<String, Object>> entities,
            Map<String, ImageFormatDefinition> formats)
    {
        // Step 1 : add image formats defined explicitly for some entities
        for (String formatKey : formats.keySet()) {
            ImageFormatDefinition imageFormatDefinition = formats.get(formatKey);
            if (imageFormatDefinition.getEntities().isPresent()) {
                for (String entity : imageFormatDefinition.getEntities().get()) {
                    addImageFormatDefinitionToEntity(entities, entity, "theme", formatKey, imageFormatDefinition);
                }
            }
        }
        // Step 2 add image formats defined for all entities
        for (String modelId : formats.keySet()) {
            ImageFormatDefinition imageFormatDefinition = formats.get(modelId);
            if (!imageFormatDefinition.getEntities().isPresent()) {
                for (String entity : entities.keySet()) {
                    addImageFormatDefinitionToEntity(entities, entity, "theme", modelId, imageFormatDefinition);
                }
            }
        }
    }

    private void addModels(Map<String, Map<String, Object>> entities, Map<String, Model> models)
    {
        // Step 1 : add models defined explicitly for some entities

        for (String modelId : models.keySet()) {
            Model model = models.get(modelId);
            if (model.getEntities().isPresent()) {
                for (String entity : model.getEntities().get()) {
                    addModelsToEntity(entities, entity, modelId, model);
                }
            }
        }
        // Step 2 add addon groups for all entities
        for (String modelId : models.keySet()) {
            Model model = models.get(modelId);
            if (!model.getEntities().isPresent()) {
                for (String entity : entities.keySet()) {
                    addModelsToEntity(entities, entity, modelId, model);
                }
            }
        }
    }

    private void addAddons(Map<String, Map<String, Object>> entities, Map<String, AddonGroupDefinition> addons,
            AddonSource source)
    {
        // Step 1 : add addon groups defined explicitly for some entities
        for (String groupKey : addons.keySet()) {
            AddonGroupDefinition group = addons.get(groupKey);
            if (group.getEntities().isPresent()) {
                for (String entity : group.getEntities().get()) {
                    addAddonGroupToEntity(entities, entity, groupKey, group, source);
                }
            }
        }
        // Step 2 add addon groups for all entities
        for (String groupKey : addons.keySet()) {
            AddonGroupDefinition group = addons.get(groupKey);
            if (!group.getEntities().isPresent()) {
                for (String entity : entities.keySet()) {
                    addAddonGroupToEntity(entities, entity, groupKey, group, source);
                }
            }
        }
    }

    private interface EntitiesMapTransformation
    {
        void apply(Map<String, Object> entity, Object... arguments);
    }

    private void addImageFormatDefinitionToEntity(Map<String, Map<String, Object>> entities, String entity,
            final String source, String modelId, ImageFormatDefinition imageFormatDefinition)
    {
        this.transformEntitiesMap(entities, entity, new EntitiesMapTransformation()
        {
            @Override
            public void apply(Map<String, Object> entity, Object... arguments)
            {
                Map map;
                if (entity.containsKey("images")) {
                    map = (Map) entity.get("images");
                } else {
                    map = Maps.newHashMap();
                }
                if (!map.containsKey(source)) {
                    map.put(source, Maps.newHashMap());
                }
                ((Map) map.get(source)).put(arguments[0], arguments[1]);
                entity.put("images", map);
            }
        }, modelId, imageFormatDefinition);
    }

    private void addModelsToEntity(Map<String, Map<String, Object>> entities, String entity, String modelId,
            Model model)
    {
        this.transformEntitiesMap(entities, entity, new EntitiesMapTransformation()
        {
            @Override
            public void apply(Map<String, Object> entity, Object... arguments)
            {
                if (entity.containsKey("models")) {
                    ((Map) entity.get("models")).put(arguments[0], arguments[1]);
                } else {
                    Map map = Maps.newHashMap();
                    map.put(arguments[0], arguments[1]);
                    entity.put("models", map);
                }
            }
        }, modelId, model);
    }

    private void addAddonGroupToEntity(Map<String, Map<String, Object>> entities, String entity, String groupKey,
            AddonGroupDefinition group, AddonSource source)
    {
        this.transformEntitiesMap(entities, entity, new EntitiesMapTransformation()
        {
            @Override
            public void apply(Map<String, Object> entity, Object... arguments)
            {
                if (!entity.containsKey("addons")) {
                    entity.put("addons", Maps.newHashMap());
                }
                Map sources = (Map) entity.get("addons");
                addAddonGroupToSource(sources, arguments);
            }

            private void addAddonGroupToSource(Map sources, Object... arguments)
            {
                if (!sources.containsKey(arguments[0].toString().toLowerCase())) {
                    sources.put(arguments[0].toString().toLowerCase(), Maps.newHashMap());
                }
                Map source = (Map) sources.get(arguments[0].toString().toLowerCase());
                source.put(arguments[1], arguments[2]);
            }
        }, source, groupKey, group);
    }

    private void transformEntitiesMap(Map<String, Map<String, Object>> entities, String entity,
            EntitiesMapTransformation transfornation,
            Object... argument)
    {
        Map<String, Object> entityMap;
        if (!entities.containsKey(entity)) {
            entityMap = Maps.newLinkedHashMap();
            entities.put(entity, entityMap);
        }
        entityMap = entities.get(entity);
        transfornation.apply(entityMap, argument);
    }
}
