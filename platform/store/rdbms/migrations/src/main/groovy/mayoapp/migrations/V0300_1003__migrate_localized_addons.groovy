/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.migrations

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration
import groovy.transform.CompileStatic
import org.mayocat.flyway.migrations.PG_UUID

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

/**
 * @version $Id$
 */
@CompileStatic
class V0300_1003__migrate_localized_addons implements JdbcMigration
{
    @Override
    void migrate(Connection connection) throws Exception
    {
        connection.setAutoCommit(false)

        Statement queryStatement = connection.createStatement()
        ResultSet data = queryStatement.executeQuery("SELECT * from localized_entity")

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> updated = [];

        while (data.next()) {
            UUID id = (UUID) data.getObject("entity_id")
            String entity = data.getString("entity")

            Map<String, Object> value = mapper.
                    readValue(entity, new TypeReference<Map<String, Object>>() {}) as Map<String, Object>;
            if (value.get("addons") != null) {
                Map<String, Object> newAddons = [:];
                List<Map<String, Object>> addons = value.get("addons") as List<Map<String, Object>>;

                for (Map<String, Object> addon : addons) {

                    if (!newAddons.containsKey(addon.get("group"))) {
                        newAddons.put(addon.get("group") as String, new HashMap<String, Object>());
                    }

                    Map<String, Object> groupMap = newAddons.get(addon.get("group") as String) as Map<String, Object>;

                    groupMap.put(addon.get("key") as String, addon.get("value"));
                }
                Map<String, Object> cloned = new HashMap<String, Object>(value);
                cloned.put("addons", newAddons)
                cloned.put("entity_id", id)
                updated << cloned
            }
        }

        queryStatement.close();

        PreparedStatement statement = connection.prepareStatement(
                "UPDATE localized_entity SET entity = CAST (? AS json) WHERE entity_id =?");

        for (Map<String, Object> clone : updated) {
            statement.setObject(2, new PG_UUID(clone.remove("entity_id") as UUID));
            statement.setObject(1, mapper.writeValueAsString(clone));
            statement.addBatch()
        }

        statement.executeBatch();
    }
}
