package org.mayocat.store.rdbms.dbi.argument;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.postgresql.util.PGobject;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @version $Id$
 */
public class JsonArgumentAsJsonArgumentFactory implements ArgumentFactory<JsonArgument>
{
    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx)
    {
        if (value == null) {
            return false;
        }
        return JsonArgument.class.isAssignableFrom(value.getClass());
    }

    @Override
    public Argument build(Class<?> expectedType, final JsonArgument value, StatementContext ctx)
    {
        try {
            final PGobject jsonObject = new PGobject();
            final ObjectMapper mapper = new ObjectMapper();

            jsonObject.setType("json");
            jsonObject.setValue(mapper.writeValueAsString(value.getWrapped()));

            return new Argument()
            {
                @Override
                public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException
                {
                    statement.setObject(position, jsonObject);
                }
            };
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to map JSON argument", e);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to map JSON argument", e);
        }
    }
}
