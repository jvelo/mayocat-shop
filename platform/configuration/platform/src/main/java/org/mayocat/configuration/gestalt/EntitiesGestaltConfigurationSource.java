package org.mayocat.configuration.gestalt;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.addons.model.AddonDefinition;
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
        List<AddonDefinition> addons = theme.getAddons();

        for (AddonDefinition addon : addons) {
            if (addon.getEntities().isPresent()) {
                for (String entity : addon.getEntities().get()) {
                    Map<String, Object> entityMap;
                    if (!entities.containsKey(entity)) {
                        entityMap = Maps.newLinkedHashMap();
                        entities.put(entity, entityMap);
                    }
                    entityMap = entities.get(entity);
                    if (entityMap.containsKey("addons")) {
                        ((List) entityMap.get("addons")).add(addon);
                    } else {
                        entityMap.put("addons", Lists.newArrayList(addon));
                    }
                }
            }
        }
        return entities;
    }
}
