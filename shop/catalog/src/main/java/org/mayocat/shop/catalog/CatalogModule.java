/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
