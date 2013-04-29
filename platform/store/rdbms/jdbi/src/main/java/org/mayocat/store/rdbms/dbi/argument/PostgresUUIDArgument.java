package org.mayocat.store.rdbms.dbi.argument;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;

/**
 * @version $Id$
 */
public class PostgresUUIDArgument implements Argument
{
    public static final String UUID_TYPE = "uuid";

    private class PG_UUID extends org.postgresql.util.PGobject
    {
        private static final long serialVersionUID = -7156483910839904635L;

        public PG_UUID(String s) throws java.sql.SQLException
        {
            super();
            this.setType(UUID_TYPE);
            this.setValue(s);
        }
    }

    private UUID value = null;

    public PostgresUUIDArgument(UUID value)
    {
        this.value = value;
    }

    @Override
    public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException
    {
        statement.setObject(position, new PG_UUID(value.toString()));
    }
}
