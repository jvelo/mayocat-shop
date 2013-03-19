package org.mayocat.configuration.gestalt;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.addons.model.AddonGroup;
import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.context.Execution;
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
    private Execution execution;

    @Override
    public Object get()
    {
        Map<String, Map<String, Object>> entities = Maps.newLinkedHashMap();

        Theme theme = execution.getContext().getTheme();
        Map<String, AddonGroup> addons = theme.getAddons();

        // Step 1 : add addon groups specified for some entities

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

        Map<String, Model> models = theme.getModels();

        // Step 1 : add models for some entities

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

        // FIXME: Need a way to list all entities
        // -> this will be implemented by the notion of "module"

        return entities;
    }

    private interface EntitiesMapTransformation
    {
        void apply(Map<String, Object> entity, Object... arguments);
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
