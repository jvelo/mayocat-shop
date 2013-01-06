package org.mayocat.shop.store.rdbms.dbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.shop.model.Tenant;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class TenantMapper implements ResultSetMapper<Tenant>
{

    @Override
    public Tenant map(int index, ResultSet result, StatementContext statementContext) throws SQLException
    {
        Tenant tenant = new Tenant(result.getLong("id"));
        tenant.setSlug(result.getString("slug"));
        return tenant;
    }
    
}
