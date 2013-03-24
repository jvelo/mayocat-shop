package org.mayocat.shop.catalog.meta;

import org.mayocat.meta.EntityMeta;
import org.mayocat.shop.catalog.model.Product;

/**
 * @version $Id$
 */
public class ProductEntity implements EntityMeta
{
    @Override
    public String getEntityName()
    {
        return "product";
    }

    @Override
    public Class getEntityClass()
    {
        return Product.class;
    }
}
