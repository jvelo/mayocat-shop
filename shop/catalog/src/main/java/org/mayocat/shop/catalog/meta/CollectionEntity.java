package org.mayocat.shop.catalog.meta;

import org.mayocat.meta.EntityMeta;
import org.mayocat.shop.catalog.model.Collection;

/**
 * @version $Id$
 */
public class CollectionEntity implements EntityMeta
{
    @Override
    public String getEntityName()
    {
        return "collection";
    }

    @Override
    public Class getEntityClass()
    {
        return Collection.class;
    }
}
