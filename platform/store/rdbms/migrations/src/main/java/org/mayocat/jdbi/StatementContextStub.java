/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.jdbi;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import org.skife.jdbi.v2.Binding;
import org.skife.jdbi.v2.Cleanable;
import org.skife.jdbi.v2.StatementContext;

/**
 * StatementContext stub to allow using JDBi mappers in flyway JDBC migrations.
 *
 * NOTE: of course, since this stub does nothing, it might break some mappers that rely on the statement context.
 * Use at your own risk for mappers that you know won't break with a stub context.
 *
 * @version $Id$
 */
public class StatementContextStub implements StatementContext
{
    @Override public Object setAttribute(String key, Object value)
    {
        return null;
    }

    @Override public Object getAttribute(String key)
    {
        return null;
    }

    @Override public Map<String, Object> getAttributes()
    {
        return null;
    }

    @Override public String getRawSql()
    {
        return null;
    }

    @Override public String getRewrittenSql()
    {
        return null;
    }

    @Override public String getLocatedSql()
    {
        return null;
    }

    @Override public PreparedStatement getStatement()
    {
        return null;
    }

    @Override public Connection getConnection()
    {
        return null;
    }

    @Override public Binding getBinding()
    {
        return null;
    }

    @Override public Class<?> getSqlObjectType()
    {
        return null;
    }

    @Override public Method getSqlObjectMethod()
    {
        return null;
    }

    @Override public boolean isReturningGeneratedKeys()
    {
        return false;
    }

    @Override public void addCleanable(Cleanable cleanable)
    {

    }
}
