package org.mayocat.configuration.gestalt;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.addons.model.AddonGroup;
import org.mayocat.configuration.GestaltConfigurationSource;
import org.mayocat.context.Execution;
import org.mayocat.theme.Theme;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;

import com.google.common.collect.Lists;
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

        // FIXME: Need a way to list all entities

        return entities;
    }

    private void addAddonGroupToEntity(Map<String, Map<String, Object>> entities, String entity, String groupKey,
            AddonGroup group)
    {
        Map<String, Object> entityMap;
        if (!entities.containsKey(entity)) {
            entityMap = Maps.newLinkedHashMap();
            entities.put(entity, entityMap);
        }
        entityMap = entities.get(entity);
        if (entityMap.containsKey("addons")) {
            ((Map) entityMap.get("addons")).put(groupKey, group);
        } else {
            Map map = Maps.newHashMap();
            map.put(groupKey, group);
            entityMap.put("addons", map);
        }
    }
}
