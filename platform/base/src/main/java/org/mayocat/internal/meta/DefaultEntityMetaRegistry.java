package org.mayocat.internal.meta;

import java.util.List;

import org.mayocat.meta.EntityMeta;
import org.mayocat.meta.EntityMetaRegistry;

/**
 * @version $Id$
 */
public class DefaultEntityMetaRegistry implements EntityMetaRegistry
{
    private List<EntityMeta> entities;

    public DefaultEntityMetaRegistry(List<EntityMeta> entities)
    {
        this.entities = entities;
    }

    @Override
    public List<EntityMeta> getEntities()
    {
        return entities;
    }
}
