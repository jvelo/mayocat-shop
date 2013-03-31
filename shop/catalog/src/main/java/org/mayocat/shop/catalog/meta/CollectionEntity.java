package org.mayocat.shop.catalog.meta;

import org.mayocat.meta.EntityMeta;
import org.mayocat.shop.catalog.model.Collection;

/**
 * @version $Id$
 */
public class CollectionEntity implements EntityMeta
{
    public static final String ID = "collection";

    public static final String PATH = "collection";

    @Override
    public String getEntityName()
    {
        return ID;
    }

    @Override
    public Class getEntityClass()
    {
        return Collection.class;
    }
}
