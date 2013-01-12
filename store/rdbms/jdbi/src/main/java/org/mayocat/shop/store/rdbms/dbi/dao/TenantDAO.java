package org.mayocat.shop.store.rdbms.dbi.dao;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.rdbms.dbi.mapper.TenantMapper;
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

}
