package org.mayocat.accounts.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.accounts.model.Role;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class RoleMapper implements ResultSetMapper<Role>
{

    @Override
    public Role map(int index, ResultSet result, StatementContext context) throws SQLException
    {
        String code = result.getString("role");
        for (Role role : Role.values()) {
            if (role.toString().equals(code)) {
                return role;
            }
        }
        return null;
    }

}
