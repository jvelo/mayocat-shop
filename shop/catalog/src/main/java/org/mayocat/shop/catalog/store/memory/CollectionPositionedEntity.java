package org.mayocat.shop.catalog.store.memory;

import java.util.ArrayList;
import java.util.List;

import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.store.memory.DefaultPositionedEntity;

/**
 * Doc goes here.
 *
 * @version $Id$
 */
public class CollectionPositionedEntity extends DefaultPositionedEntity<Collection>
{
    private List<Product> products = new ArrayList<>();

    public CollectionPositionedEntity(Collection entity, Integer position, List<Product> products)
    {
        super(entity, position);
    }

    public List<Product> getProducts()
    {
        return products;
    }
}
