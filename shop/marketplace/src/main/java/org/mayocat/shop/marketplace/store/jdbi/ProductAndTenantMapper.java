/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.store.jdbi;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.model.Entity;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.jdbi.mapper.ProductMapper;
import org.skife.jdbi.v2.StatementContext;

/**
 * @version $Id$
 */
public class ProductAndTenantMapper extends AbstractEntityAndTenantMapper
{
    public Product extractEntity(int index, ResultSet result, StatementContext ctx) throws SQLException
    {
        ProductMapper productMapper = new ProductMapper();
        return productMapper.map(index, result, ctx);
    }
}
