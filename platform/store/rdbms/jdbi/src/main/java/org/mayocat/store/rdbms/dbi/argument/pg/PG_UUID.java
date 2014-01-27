package org.mayocat.store.rdbms.dbi.argument.pg;

/**
 * Postgres UUID jdbc object
 *
 * @version $Id$
 */
public class PG_UUID extends org.postgresql.util.PGobject
{
    public static final String UUID_TYPE = "uuid";

    public PG_UUID(String s) throws java.sql.SQLException
    {
        super();
        this.setType(UUID_TYPE);
        this.setValue(s);
    }
}

