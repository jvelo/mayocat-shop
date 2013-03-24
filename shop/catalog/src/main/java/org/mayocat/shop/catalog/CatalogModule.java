package org.mayocat.shop.catalog;

import java.util.ArrayList;
import java.util.List;

import org.mayocat.meta.EntityMeta;
import org.mayocat.Module;
import org.mayocat.shop.catalog.meta.CollectionEntity;
import org.mayocat.shop.catalog.meta.ProductEntity;

/**
 * @version $Id$
 */
public class CatalogModule implements Module
{
    private static final List<EntityMeta> entities = new ArrayList<EntityMeta>();

    static {
        entities.add(new ProductEntity());
        entities.add(new CollectionEntity());
    }

    @Override
    public String getName()
    {
        return "catalog";
    }

    @Override
    public List<EntityMeta> getEntities()
    {
        return entities;
    }
}
