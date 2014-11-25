/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.shop.billing.model.OrderSummary;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @version $Id$
 */
public class OrderSummaryMapper extends AbstractOrderMapper implements ResultSetMapper<OrderSummary>
{
    public OrderSummary map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        OrderSummary order = new OrderSummary();
        fillOrderSummary(resultSet, order);
        return order;
    }
}
