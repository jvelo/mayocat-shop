package org.mayocat.store.rdbms.dbi.argument;

import java.util.UUID;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

/**
 * @version $Id$
 */
public class PostgresUUIDArgumentFactory implements ArgumentFactory<UUID>
{
    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx)
    {
        if (value == null) {
            return false;
        }
        return UUID.class.isAssignableFrom(value.getClass());
    }

    @Override
    public Argument build(Class<?> expectedType, UUID value, StatementContext ctx)
    {
        return new PostgresUUIDArgument(value);
    }
}
