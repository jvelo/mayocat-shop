package org.mayocat.accounts.store.jdbi.dao;

import java.util.List;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.store.jdbi.mapper.TenantMapper;
import org.mayocat.store.rdbms.dbi.dao.EntityDAO;
import org.mayocat.store.rdbms.jdbi.AddonsDAO;
import org.mayocat.store.rdbms.jdbi.AddonsHelper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

@RegisterMapper(TenantMapper.class)
@UseStringTemplate3StatementLocator
public abstract class TenantDAO implements EntityDAO<Tenant>, Transactional<TenantDAO>, AddonsDAO<Tenant>
{
    @SqlUpdate
    (
        "UPDATE tenant " +
        "SET    configuration = :data, " +
        "       configuration_version = :version " +
        "WHERE  entity_id = tenant.id"
    )
    public abstract void updateConfiguration(@BindBean("tenant") Tenant tenant, @Bind("version") Integer version,
            @Bind("data") String configuration);

    @SqlUpdate
    (
        "INSERT INTO tenant " +
        "            (entity_id," +
        "             default_host," +
        "             configuration," +
        "             configuration_version) " +
        "VALUES      (:tenant.id, " +
        "             :tenant.defaultHost," +
        "             :data," +
        "             :version ) "
    )
    public abstract void create(@BindBean("tenant") Tenant tenant,  @Bind("version") Integer version,
            @Bind("data") String configuration);

    @SqlQuery
    (
        "SELECT * " +
        "FROM   entity " +
        "       INNER JOIN tenant " +
        "               ON entity.id = tenant.entity_id " +
        "WHERE  default_host = :host "
    )
    public abstract Tenant findByDefaultHost(@Bind("host") String host);

    @SqlQuery
    (
        "SELECT * " +
        "FROM   tenant " +
        "LIMIT  :number " +
        "OFFSET :offset "
    )
    public abstract List<Tenant> findAll(@Bind("number") Integer number, @Bind("offset") Integer offset);

    public void createOrUpdateAddons(Tenant entity)
    {
        AddonsHelper.createOrUpdateAddons(this, entity);
    }
}
