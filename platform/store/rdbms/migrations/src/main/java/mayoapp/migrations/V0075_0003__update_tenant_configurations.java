/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.migrations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

import org.mayocat.flyway.migrations.PG_UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;

/**
 * Migrates tenant configuration : extract the tenant name from the configuration to put it directly in the
 * new "name" column.
 *
 * @version $Id$
 */
public class V0075_0003__update_tenant_configurations implements JdbcMigration
{
    private class ConfigurationAndName {
        private String configuration;
        private String name;

        private ConfigurationAndName(String configuration, String name)
        {
            this.configuration = configuration;
            this.name = name;
        }

        public String getConfiguration()
        {
            return configuration;
        }

        public String getName()
        {
            return name;
        }
    }

    @Override
    public void migrate(Connection connection) throws Exception
    {
        connection.setAutoCommit(false);

        Statement queryIdsStatement = connection.createStatement();
        ResultSet tenants = queryIdsStatement.executeQuery("SELECT entity_id, slug, configuration FROM entity " +
                "INNER JOIN tenant ON entity.id = tenant.entity_id");

        Map<UUID, ConfigurationAndName> tenantsData = Maps.newHashMap();

        while (tenants.next()) {
            String json = tenants.getString("configuration");
            String name = tenants.getString("slug");
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> configuration = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
            if (configuration.containsKey("general")) {
                Map<String, Object> generalConfiguration = (Map<String, Object>) configuration.get("general");
                if (generalConfiguration.containsKey("name")) {
                    name = (String) generalConfiguration.get("name");
                    ((Map<String, Object>) configuration.get("general")).remove("name");
                    json = objectMapper.writeValueAsString(configuration);
                }

            }
            ConfigurationAndName configurationAndName = new ConfigurationAndName(json, name);
            tenantsData.put((UUID) tenants.getObject("entity_id"), configurationAndName);
        }

        queryIdsStatement.close();

        PreparedStatement statement = connection.prepareStatement(
                "UPDATE tenant SET name=?, configuration=? WHERE entity_id =?");

        for (UUID id : tenantsData.keySet())  {
            statement.setString(1, tenantsData.get(id).getName());
            statement.setString(2, tenantsData.get(id).getConfiguration());
            statement.setObject(3, new PG_UUID(id));
            statement.addBatch();
        }

        try {
            statement.executeBatch();
        } finally {
            statement.close();
        }
    }
}
