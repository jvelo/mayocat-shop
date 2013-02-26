package org.mayocat.accounts.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.accounts.model.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class UserMapper implements ResultSetMapper<User>
{
    @Override
    public User map(int index, ResultSet result, StatementContext statementContext) throws SQLException
    {
        User user = new User(result.getLong("id"));
        user.setEmail(result.getString("email"));
        user.setPassword(result.getString("password"));
        user.setSlug(result.getString("slug"));
        return user;
    }
}
