package org.mayocat.accounts.store.jdbi.dao;

import java.util.List;
import java.util.UUID;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.store.jdbi.mapper.TenantMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

@RegisterMapper(TenantMapper.class)
public abstract class TenantDAO implements Transactional<TenantDAO>
{
    @SqlUpdate
    (
        "INSERT INTO configuration " +
        "            (id," +
        "             version, " +
        "             data) " +
        "VALUES      (:id," +
        "             :version, " +
        "             :data) "
    )
    public abstract Integer createConfiguration(@Bind("id") UUID id, @Bind("version") Integer version,
            @Bind("data") String configuration);

    @SqlUpdate
    (
        "UPDATE configuration " +
        "SET    data = :data, " +
        "       version = :version " +
        "WHERE  id = (SELECT configuration_id " +
        "             FROM   tenant " +
        "             WHERE  id = :tenant.id)"
    )
    public abstract void updateConfiguration(@BindBean("tenant") Tenant tenant, @Bind("version") Integer version,
            @Bind("data") String configuration);

    @SqlUpdate
    (
        "INSERT INTO tenant " +
        "            (id," +
        "             slug, " +
        "             configuration_id) " +
        "VALUES      (:tenant.id, " +
        "             :tenant.slug, " +
        "             :configuration) "
    )
    public abstract void create(@BindBean("tenant") Tenant tenant, @Bind("configuration") UUID configuration);

    @SqlQuery
    (
        "SELECT * " +
        "FROM   tenant " +
        "       INNER JOIN configuration " +
        "               ON tenant.configuration_id = configuration.id " +
        "WHERE  slug = :slug "
    )
    public abstract Tenant findBySlug(@Bind("slug") String slug);

    @SqlQuery
    (
        "SELECT * " +
        "FROM   tenant " +
        "       INNER JOIN configuration " +
        "               ON tenant.configuration_id = configuration.id " +
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
}
