/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
