/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi.argument;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.UUID;

import org.mayocat.store.rdbms.dbi.argument.pg.PG_UUID;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

/**
 * Argument factory for a list of UUID.
 *
 * @version $Id$
 */
public class PostgresUUIDArrayArgumentFactory implements ArgumentFactory<List<UUID>>
{
    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx)
    {
        if (value == null) {
            return false;
        }

        if (!List.class.isAssignableFrom(value.getClass())) {
            return false;
        }

        List list = (List) value;

        if (list.size() == 0 || list.get(0) == null) {
            // This is not efficient since we are forcing an exception just to know if UUID is the right type.
            // Right now I don't have any better idea since at this stage the list parametrized type is erased.
            // Please fix this code if you know a better way.
            try {
                if (list.add(UUID.randomUUID())) {
                    list.remove(list.size() - 1);
                    return true;
                }
            } catch (ClassCastException e) {
                return false;
            }
        }

        return UUID.class.isAssignableFrom(list.get(0).getClass());
    }

    @Override
    public Argument build(Class<?> expectedType, final List<UUID> value, StatementContext ctx)
    {
        return new Argument()
        {
            @Override
            public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException
            {
                Integer length = value.size();

                PG_UUID[] uuids = new PG_UUID[length];

                for (int i = 0; i < length; i++) {
                    uuids[i] = new PG_UUID(value.get(i).toString());
                }

                Array ary = ctx.getConnection().createArrayOf("uuid", uuids);

                statement.setArray(position, ary);
            }
        };
    }
}
