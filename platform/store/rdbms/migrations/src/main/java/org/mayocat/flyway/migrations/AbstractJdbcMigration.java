package org.mayocat.flyway.migrations;

import java.sql.SQLException;
import java.util.UUID;

import org.postgresql.util.PGobject;

public class AbstractJdbcMigration
{
    protected class PG_UUID extends PGobject
    {
        private static final long serialVersionUID = -3049777497876782935L;

        public PG_UUID(UUID id) throws SQLException
        {
            this(id.toString());
        }

        public PG_UUID(String s) throws SQLException
        {
            super();
            this.setType("uuid");
            this.setValue(s);
        }
    }
}