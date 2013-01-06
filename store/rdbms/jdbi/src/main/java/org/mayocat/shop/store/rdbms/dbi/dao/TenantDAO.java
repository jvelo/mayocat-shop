package org.mayocat.shop.store.rdbms.dbi.dao;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.rdbms.dbi.mapper.TenantMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(TenantMapper.class)
public abstract class TenantDAO
{
    @SqlUpdate("INSERT INTO tenant (slug) VALUES (:slug)")
    public abstract void create(@BindBean Tenant tenant);

    @SqlQuery("SELECT id, slug FROM tenant WHERE slug=:slug")
    public abstract Tenant findBySlug(@Bind("slug") String slug);

}
