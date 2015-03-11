/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.flyway.migrations

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration
import groovy.transform.CompileStatic

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

/**
 * Create entity_list for all products images.
 *
 * @version $Id$
 */
@CompileStatic
abstract class AbstractImageGalleryEntityListsMigration implements JdbcMigration {

    /**
     * Must return the table/entity the concrete migration will operate on
     */
    abstract String getTable();

    void migrate(Connection connection) throws Exception
    {
        connection.setAutoCommit(false);

        Statement s = connection.createStatement()
        ResultSet entities = s.executeQuery("SELECT e.id, e.tenant_id FROM entity AS e JOIN " + getTable()
                + " AS p ON p.entity_id = e.id");

        def entitiesIds = [:] as Map<UUID, UUID>

        while (entities.next()) {
            entitiesIds.put((entities.getObject("id") as UUID), (entities.getObject("tenant_id") as UUID))
        }

        s.close();

        entitiesIds.keySet().each({ UUID entityId ->

            def imagesIds = [] as List<UUID>
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM attachment" +
                    " join entity as ae on attachment.entity_id = ae.id where ae.parent_id = ?");
            statement.setObject(1, new PG_UUID(entityId));
            ResultSet images = statement.executeQuery();

            while (images.next()) {
                imagesIds << (images.getObject("id") as UUID)
            }

            statement.close();

            if (imagesIds.size() > 0) {
                UUID listId = UUID.randomUUID();
                UUID tenantId = entitiesIds.get(entityId)

                // 1. Entity

                statement = connection.prepareStatement("INSERT INTO entity VALUES (?, 'image-gallery', 'entity_list', ?, ?)")
                statement.setObject(1, new PG_UUID(listId));
                statement.setObject(2, new PG_UUID(tenantId));
                statement.setObject(3, new PG_UUID(entityId));
                statement.execute();
                statement.close();

                // 2. Entity list

                statement = connection.prepareStatement("INSERT INTO entity_list VALUES (?, ?, ?, 'image_gallery')")

                statement.setObject(1, new PG_UUID(listId));
                statement.setString(2, getTable());

                PG_UUID[] uuids = new PG_UUID[imagesIds.size()];

                for (int i = 0; i < imagesIds.size(); i++) {
                    uuids[i] = new PG_UUID(imagesIds[i]);
                }

                statement.setArray(3, connection.createArrayOf("uuid", uuids));
                statement.execute();
                statement.close();
            }
        })
    }
}