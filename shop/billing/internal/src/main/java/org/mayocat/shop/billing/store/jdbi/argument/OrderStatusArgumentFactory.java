/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.store.jdbi.argument;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mayocat.shop.billing.model.Order;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

/**
 * @version $Id$
 */
public class OrderStatusArgumentFactory implements ArgumentFactory<Order.Status>
{
    @Override public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx)
    {
        if (value == null) {
            return false;
        }
        return Order.Status.class.isAssignableFrom(value.getClass());
    }

    @Override public Argument build(Class<?> expectedType, final Order.Status value, StatementContext ctx)
    {
        return new Argument()
        {
            @Override
            public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException
            {
                statement.setString(position, value.toString());
            }
        };
    }
}
