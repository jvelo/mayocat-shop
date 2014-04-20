/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi.argument;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

/**
 * Argument factory that binds java.util.Date as timestamp.
 *
 * See https://groups.google.com/d/msg/jdbi/ooFw_s183jM/WLwNBJuemYEJ
 *
 * @version $Id$
 */
public class DateAsTimestampArgumentFactory implements ArgumentFactory<Date>
{
    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx)
    {
        if (value == null) {
            return false;
        }
        return java.util.Date.class.isAssignableFrom(value.getClass());
    }

    @Override
    public Argument build(Class<?> expectedType, final Date value, StatementContext ctx)
    {
        return new Argument()
        {
            @Override
            public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException
            {
                statement.setTimestamp(position, new java.sql.Timestamp(value.getTime()));
            }
        };
    }
}
