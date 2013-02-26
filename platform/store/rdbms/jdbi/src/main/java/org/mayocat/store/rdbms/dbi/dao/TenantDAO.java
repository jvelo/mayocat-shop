package org.mayocat.store.rdbms.dbi.dao;

import java.util.List;

import org.mayocat.model.Tenant;
import org.mayocat.store.rdbms.dbi.mapper.TenantMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

@RegisterMapper(TenantMapper.class)
public abstract class TenantDAO implements Transactional<TenantDAO>
{
    @GetGeneratedKeys
    @SqlUpdate
    (
        "INSERT INTO configuration " +
        "            (version, " +
        "             data) " +
        "VALUES      (:version, " +
        "             :data) "
    )
    public abstract Integer createConfiguration(@Bind("version") Integer version, @Bind("data") String configuration);

    @SqlUpdate
    (
        "UPDATE configuration " +
        "SET    data = :data, " +
        "       version = :version " +
        "WHERE  id = (SELECT configuration_id " +
        "             FROM   tenant " +
        "             WHERE  id = tenant.id) "
    )
    public abstract void updateConfiguration(@BindBean("tenant") Tenant id, @Bind("version") Integer version,
            @Bind("data") String configuration);

    @SqlUpdate
    (
        "INSERT INTO tenant " +
        "            (slug, " +
        "             configuration_id) " +
        "VALUES      (:tenant.slug, " +
        "             :configuration) "
    )
    public abstract void create(@BindBean("tenant") Tenant tenant, @Bind("configuration") Integer configuration);

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
        "LIMIT  :number " +
        "OFFSET :offset "
    )
    public abstract List<Tenant> findAll(@Bind("number") Integer number, @Bind("offset") Integer offset);
}
