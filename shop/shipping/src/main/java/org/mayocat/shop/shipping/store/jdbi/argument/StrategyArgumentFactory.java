package org.mayocat.shop.shipping.store.jdbi.argument;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mayocat.shop.shipping.Strategy;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

/**
 * @version $Id$
 */
public class StrategyArgumentFactory implements ArgumentFactory<Strategy>
{
    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx)
    {
        if (value == null) {
            return false;
        }
        return Strategy.class.isAssignableFrom(value.getClass());
    }

    @Override
    public Argument build(Class<?> expectedType, final Strategy value, StatementContext ctx)
    {
        return new Argument()
        {
            @Override
            public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException
            {
                statement.setString(position, value.toJson());
            }
        };
    }
}
