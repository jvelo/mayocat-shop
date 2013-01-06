package org.mayocat.shop.store.rdbms.dbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.shop.model.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class UserMapper implements ResultSetMapper<User>
{

    @Override
    public User map(int index, ResultSet result, StatementContext statementContext) throws SQLException
    {
        User user = new User(result.getLong("entity.id"));
        user.setEmail(result.getString("user.email"));
        user.setPassword(result.getString("user.password"));
        user.setSlug(result.getString("entity.slug"));
        return user;
    }

}
