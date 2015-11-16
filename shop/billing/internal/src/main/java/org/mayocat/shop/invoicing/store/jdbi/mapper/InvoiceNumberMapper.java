/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.invoicing.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.mayocat.shop.invoicing.model.InvoiceNumber;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @version $Id$
 */
public class InvoiceNumberMapper implements ResultSetMapper<InvoiceNumber>
{
    @Override
    public InvoiceNumber map(int index, ResultSet r, StatementContext ctx) throws SQLException
    {
        return new InvoiceNumber(
                (UUID) r.getObject("order_id"),
                r.getString("number"),
                r.getTimestamp("generation_date")
        );
    }
}
