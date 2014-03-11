/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.store.memory;

import java.util.ArrayList;
import java.util.List;

import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.store.memory.DefaultPositionedEntity;

/**
 * Collection entity for memory store.
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
