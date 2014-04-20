/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi.mapper;

import java.sql.ResultSet;
import java.util.Map;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * Same as org.skife.jdbi.v2.BaseResultSetMapper, with {@link TableColumnMapMapper} as the wrapped mapper 
 */
public abstract class BaseMapMapper<ResultType> implements ResultSetMapper<ResultType>
{
    private static final TableColumnMapMapper mapper = new TableColumnMapMapper();

    /**
     * Defers to mapInternal
     */
    public final ResultType map(int index, ResultSet r, StatementContext ctx)
    {
        return this.mapInternal(index, mapper.map(index, r, ctx));
    }

    /**
     * Subclasses should implement this method in order to map the result
     *
     * @param index The row, starting at 0
     * @param row The result of a {@link org.skife.jdbi.v2.tweak.ResultSetMapper#map} call
     * @return the value to pt into the results from a query
     */
    protected abstract ResultType mapInternal(int index, Map<String, Object> row);
}
