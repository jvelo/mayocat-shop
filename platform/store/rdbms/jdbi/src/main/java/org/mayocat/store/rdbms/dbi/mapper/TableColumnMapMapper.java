/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.exceptions.ResultSetException;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * Same as org.skife.jdbi.v2.DefaultMapper except the map has both the alias and the form <tableName>.<columnName> as keys
 */
public class TableColumnMapMapper implements ResultSetMapper<Map<String, Object>>
{

    public Map<String, Object> map(int index, ResultSet r, StatementContext ctx)
    {
        Map<String, Object> row = new DefaultResultMap();
        ResultSetMetaData m;
        try
        {
            m = r.getMetaData();
        }
        catch (SQLException e)
        {
            throw new ResultSetException("Unable to obtain metadata from result set", e, ctx);
        }

        try
        {
            for (int i = 1; i <= m.getColumnCount(); i ++)
            {
                String key = m.getColumnName(i);
                Object value = r.getObject(i);
                row.put(key, value);
            }
        }
        catch (SQLException e)
        {
            throw new ResultSetException("Unable to access specific metadata from " +
                                         "result set metadata", e, ctx);
        }
        return row;
    }

    private static class DefaultResultMap extends HashMap<String, Object>
    {
        public static final long serialVersionUID = 1L;

        @Override
        public Object get(Object o)
        {
            return super.get(((String)o).toLowerCase());
        }

        @Override
        public Object put(String key, Object value)
        {
            return super.put(key.toLowerCase(), value);
        }

        @Override
        public boolean containsKey(Object key)
        {
            return super.containsKey(((String)key).toLowerCase());
        }
    }
}
