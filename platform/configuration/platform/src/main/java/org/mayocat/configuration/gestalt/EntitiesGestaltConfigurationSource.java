package org.mayocat.configuration.gestalt;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.addons.model.AddonGroup;
import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.configuration.thumbnails.ThumbnailDefinition;
import org.mayocat.context.Execution;
import org.mayocat.meta.EntityMeta;
import org.mayocat.meta.EntityMetaRegistry;
import org.mayocat.theme.Model;
import org.mayocat.theme.Theme;
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
    private EntityMetaRegistry entityMetaRegistry;

    @Inject
    private Execution execution;

    @Override
    public Object get()
    {
        Map<String, Map<String, Object>> entities = Maps.newLinkedHashMap();
        for (EntityMeta meta : entityMetaRegistry.getEntities()) {
            Map data = Maps.newHashMap();
            entities.put(meta.getEntityName(), data);
        }

        Theme theme = execution.getContext().getTheme();

        addAddons(entities, theme.getAddons());
        addModels(entities, theme.getModels());
        addThumbnails(entities, theme.getThumbnails());

        return entities;
    }

    private void addThumbnails(Map<String, Map<String, Object>> entities, Map<String, ThumbnailDefinition> thumbnails)
    {
        // Step 1 : add thumbnails defined explicitly for some entities
        for (String thumbnailKey : thumbnails.keySet()) {
            ThumbnailDefinition thumbnailDefinition = thumbnails.get(thumbnailKey);
            if (thumbnailDefinition.getEntities().isPresent()) {
                for (String entity : thumbnailDefinition.getEntities().get()) {
                    addThumbnailDefinitionToEntity(entities, entity, "theme", thumbnailKey, thumbnailDefinition);
                }
            }
        }
        // Step 2 add thumbnails groups for all entities
        for (String modelId : thumbnails.keySet()) {
            ThumbnailDefinition thumbnailDefinition = thumbnails.get(modelId);
            if (!thumbnailDefinition.getEntities().isPresent()) {
                for (String entity : entities.keySet()) {
                    addThumbnailDefinitionToEntity(entities, entity, "theme", modelId, thumbnailDefinition);
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

    private void addAddons(Map<String, Map<String, Object>> entities, Map<String, AddonGroup> addons)
    {
        // Step 1 : add addon groups defined explicitly for some entities
        for (String groupKey : addons.keySet()) {
            AddonGroup group = addons.get(groupKey);
            if (group.getEntities().isPresent()) {
                for (String entity : group.getEntities().get()) {
                    addAddonGroupToEntity(entities, entity, groupKey, group);
                }
            }
        }
        // Step 2 add addon groups for all entities
        for (String groupKey : addons.keySet()) {
            AddonGroup group = addons.get(groupKey);
            if (!group.getEntities().isPresent()) {
                for (String entity : entities.keySet()) {
                    addAddonGroupToEntity(entities, entity, groupKey, group);
                }
            }
        }
    }

    private interface EntitiesMapTransformation
    {
        void apply(Map<String, Object> entity, Object... arguments);
    }

    private void addThumbnailDefinitionToEntity(Map<String, Map<String, Object>> entities, String entity,
            final String source, String modelId, ThumbnailDefinition thumbnailDefinition)
    {
        this.transformEntitiesMap(entities, entity, new EntitiesMapTransformation()
        {
            @Override
            public void apply(Map<String, Object> entity, Object... arguments)
            {
                Map map;
                if (entity.containsKey("thumbnails")) {
                    map = (Map) entity.get("thumbnails");
                } else {
                    map = Maps.newHashMap();
                }
                if (!map.containsKey(source)) {
                    map.put(source, Maps.newHashMap());
                }
                ((Map) map.get(source)).put(arguments[0], arguments[1]);
                entity.put("thumbnails", map);
            }
        }, modelId, thumbnailDefinition);
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
            AddonGroup group)
    {
        this.transformEntitiesMap(entities, entity, new EntitiesMapTransformation()
        {
            @Override
            public void apply(Map<String, Object> entity, Object... arguments)
            {
                if (entity.containsKey("addons")) {
                    ((Map) entity.get("addons")).put(arguments[0], arguments[1]);
                } else {
                    Map map = Maps.newHashMap();
                    map.put(arguments[0], arguments[1]);
                    entity.put("addons", map);
                }
            }
        }, groupKey, group);
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
