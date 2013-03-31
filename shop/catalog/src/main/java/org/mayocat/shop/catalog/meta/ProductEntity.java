package org.mayocat.shop.catalog.meta;

import org.mayocat.meta.EntityMeta;
import org.mayocat.shop.catalog.model.Product;

/**
 * @version $Id$
 */
public class ProductEntity implements EntityMeta
{
    public static final String ID = "product";

    public static final String PATH = "products";

    @Override
    public String getEntityName()
    {
        return ID;
    }

    @Override
    public Class getEntityClass()
    {
        return Product.class;
    }
}
