/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.flyway.migrations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;

/**
 * @version $Id$
 */
public abstract class AbstractUUIDGenerationMigration implements JdbcMigration
{
    public void migrate(Connection connection) throws Exception
    {
        for (String tableName : getTableNames()) {
            List<Long> ids = new ArrayList<Long>();

            connection.setAutoCommit(false);

            Statement queryIdsStatement = connection.createStatement();
            ResultSet idsResultSet = queryIdsStatement.executeQuery("SELECT " + getIdField() + " FROM " + tableName);

            while (idsResultSet.next()) {
                ids.add(idsResultSet.getLong(getIdField()));
            }

            queryIdsStatement.close();

            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE " + tableName + getTransitionTableSuffix() + " SET " + getUUIDField() + "=? WHERE " +
                            getIdField() + "=?");

            for (Long id : ids) {
                statement.setObject(1, new PG_UUID(UUID.randomUUID().toString()));
                statement.setLong(2, id);
                statement.addBatch();
            }

            try {
                statement.executeBatch();
            } finally {
                statement.close();
            }
        }
    }

    public String getIdField()
    {
        return "id";
    }

    public String getUUIDField()
    {
        return "uuid";
    }

    public abstract List<String> getTableNames();

    public abstract String getTransitionTableSuffix();
}
