package org.mayocat.shop.catalog.search;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.context.Execution;
import org.mayocat.model.Entity;
import org.mayocat.model.annotation.SearchIndex;
import org.mayocat.search.EntityIndexSourceMapper;
import org.slf4j.Logger;

/**
 * @version $Id$
 */
public abstract class AbstractEntityIndexSourceMapper implements EntityIndexSourceMapper
{
    @Inject
    private Logger logger;

    @Inject
    private Execution execution;

    @Override
    public Map<String, Object> mapSource(Entity entity)
    {
        return this.mapSource(entity, execution.getContext().getTenant());
    }

    protected Map<String, Object> extractSourceFromEntity(Entity entity)
    {
        Map<String, Object> source = new HashMap<String, Object>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            boolean isAccessible = field.isAccessible();
            try {
                field.setAccessible(true);
                SearchIndex searchIndex = field.getAnnotation(SearchIndex.class);
                if (searchIndex != null) {
                    source.put(field.getName(), field.get(entity));
                }
            } catch (IllegalAccessException e) {
                this.logger.error("Error extracting entity", entity.getSlug(), e);
            } finally {
                field.setAccessible(isAccessible);
            }
        }
        return source;
    }
}
