/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.mayocat.shop.catalog.model.ProductCollection;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * Mapper for {@link ProductCollection}
 *
 * @version $Id$
 */
public class ProductCollectionMapper implements ResultSetMapper<ProductCollection>
{
    public ProductCollection map(int index, ResultSet r, StatementContext ctx) throws SQLException
    {
        return new ProductCollection((UUID) r.getObject("product_id"), (UUID) r.getObject("collection_id"));
    }
}
