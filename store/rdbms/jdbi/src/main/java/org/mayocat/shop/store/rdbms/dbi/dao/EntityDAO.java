package org.mayocat.shop.store.rdbms.dbi.dao;

import java.util.List;

import org.mayocat.shop.model.Entity;
import org.mayocat.shop.model.Tenant;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * Inheriting classes MUST be annotated with {@link UseStringTemplate3StatementLocator}
 *
 * @param <E> the entity type this DAO manages.
 */
public interface EntityDAO< E extends Entity >
{

    @GetGeneratedKeys
    @SqlUpdate
    (
        "INSERT INTO entity (slug, type, tenant_id) VALUES (:entity.slug, :type, :tenant.id)"
    )
    Long createEntity(@BindBean("entity") Entity entity, @Bind("type") String type, @BindBean("tenant") Tenant tenant);
    
    @SqlQuery
    (
        "SELECT id FROM entity WHERE slug = :entity.slug AND type = :type AND tenant_id = :tenant.id"
    )
    Long getId(@BindBean("entity") Entity entity, @Bind("type") String type, @BindBean("tenant") Tenant tenant);

    @SqlQuery
    (
        "SELECT * FROM entity INNER JOIN <type> ON entity.id = <type>.entity_id " +
        "WHERE entity.id = :id"
    )
    E findById(@Define("type") String type, @Bind("id") Long id);
    
    @SqlQuery
    (
        "SELECT * FROM entity INNER JOIN <type> ON entity.id = <type>.entity_id " +
        "WHERE entity.slug = :slug AND entity.type = '<type>' AND entity.tenant_id = :tenant.id"
    )
    E findBySlug(@Define("type") String type, @Bind("slug") String slug, @BindBean("tenant") Tenant tenant);
    
    @SqlQuery
    (
        "SELECT * FROM entity INNER JOIN <type> ON entity.id = <type>.entity_id " +
        "WHERE entity.type = '<type>' AND entity.tenant_id = :tenant.id LIMIT :number OFFSET :offset"
    )
    List<E> findAll(@Define("type") String type, @BindBean("tenant") Tenant tenant, @Bind("number") Integer number, @Bind("offset") Integer offset);
}
