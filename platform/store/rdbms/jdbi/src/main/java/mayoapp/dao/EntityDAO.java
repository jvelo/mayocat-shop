/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.List;
import java.util.UUID;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Child;
import org.mayocat.model.Entity;
import org.mayocat.model.EntityList;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

/**
 * Inheriting classes MUST be annotated with {@link UseStringTemplate3StatementLocator}
 *
 * @param <E> the entity type this DAO manages.
 */
@UseStringTemplate3StatementLocator
public interface EntityDAO< E extends Entity >
{

    @SqlUpdate
    (
        "INSERT INTO entity (id, slug, type) VALUES (:entity.id, :entity.slug, :type)"
    )
    Integer createEntity(@BindBean("entity") Entity entity, @Bind("type") String type);

    @SqlUpdate
    (
        "INSERT INTO entity (id, slug, type, tenant_id) VALUES (:entity.id, :entity.slug, :type, :tenant.id)"
    )
    Integer createEntity(@BindBean("entity") Entity entity, @Bind("type") String type, @BindBean("tenant") Tenant tenant);

    @SqlUpdate
    (
        "INSERT INTO entity (id, slug, type, tenant_id) SELECT :entity.id, :entity.slug, :type, :tenant.id " +
        "WHERE NOT EXISTS (SELECT 1 FROM entity where slug = :entity.slug AND type = :type AND tenant_id = :tenant.id)"
    )
    Integer createEntityIfItDoesNotExist(@BindBean("entity") Child entity, @Bind("type") String type,
            @BindBean("tenant") Tenant tenant);


    @SqlUpdate
    (
        "INSERT INTO entity (id, slug, type, tenant_id, parent_id) VALUES (:entity.id, :entity.slug, :type, :tenant.id, :entity.parentId)"
    )
    Integer createChildEntity(@BindBean("entity") Child entity, @Bind("type") String type,
            @BindBean("tenant") Tenant tenant);

    @SqlQuery
    (
        "SELECT insert_child_entity_if_not_exist(:entity.id, :entity.slug, :type, :tenant.id, :entity.parentId)"
    )
    Integer createChildEntityIfItDoesNotExist(@BindBean("entity") Child entity, @Bind("type") String type,
            @BindBean("tenant") Tenant tenant);

    @SqlUpdate
    (
        "DELETE FROM entity WHERE entity.id = :id or entity.parent_id = :id"
    )
    Integer deleteEntityAndChildrenById(@Bind("id") UUID id);

    @SqlUpdate
    (
        "UPDATE entity SET parent_id = null WHERE parent_id = :id"
    )
    Integer detachChildren(@Bind("id") UUID id);

    @SqlUpdate
    (
        "UPDATE entity SET parent_id = null WHERE id = :id"
    )
    Integer detach(@Bind("id") UUID id);

    @SqlUpdate
    (
        "DELETE FROM <type> WHERE <type>.entity_id = :id"
    )
    Integer deleteEntityEntityById(@Define("type") String type, @Bind("id") UUID id);

    @SqlQuery
    (
        "SELECT id FROM entity WHERE slug = :entity.slug AND type = :type AND tenant_id = :tenant.id"
    )
    UUID getId(@BindBean("entity") Entity entity, @Bind("type") String type, @BindBean("tenant") Tenant tenant);

    @SqlQuery
    (
        "SELECT entity.*, <type>.*, localization_data(entity_id) FROM entity INNER JOIN <type> ON entity.id = <type>.entity_id " +
        "WHERE entity.id = :id"
    )
    E findById(@Define("type") String type, @Bind("id") UUID id);

    @SqlQuery
    (
        "SELECT entity.*, <type>.*, localization_data(entity_id) FROM entity INNER JOIN <type> ON entity.id = <type>.entity_id " +
        "WHERE entity.slug = :slug AND entity.type = '<type>' AND tenant_id is null"
    )
    E findBySlug(@Define("type") String type, @Bind("slug") String slug);

    @SqlQuery
    (
        "SELECT entity.*, <type>.*, localization_data(entity_id) FROM entity INNER JOIN <type> ON entity.id = <type>.entity_id " +
        "WHERE entity.slug = :slug AND entity.type = '<type>' AND entity.tenant_id = :tenant.id"
    )
    E findBySlug(@Define("type") String type, @Bind("slug") String slug, @BindBean("tenant") Tenant tenant);

    @SqlQuery
    (
        "SELECT entity.*, <type>.*, localization_data(entity_id) FROM entity INNER JOIN <type> ON entity.id = <type>.entity_id " +
        "WHERE entity.slug = :slug AND entity.parent_id = :parent AND entity.type = '<type>' AND tenant_id = :tenant.id"
    )
    E findBySlug(@Define("type") String type, @Bind("slug") String slug, @BindBean("tenant") Tenant tenant,
            @Bind("parent") UUID parent);

    @SqlQuery
    (
        "SELECT entity.*, <type>.*, localization_data(entity_id) " +
        "FROM entity " +
        "INNER JOIN <type> ON entity.id = <type>.entity_id " +
        "WHERE entity.type = '<type>'"
    )
    List<E> findAll(@Define("type") String type);

    @SqlQuery
    (
        "SELECT entity.*, <type>.*, localization_data(entity_id) " +
        "FROM entity " +
        "INNER JOIN <type> ON entity.id = <type>.entity_id " +
        "WHERE entity.type = '<type>'" +
        "  AND entity.tenant_id = :tenant.id"
    )
    List<E> findAll(@Define("type") String type, @BindBean("tenant") Tenant tenant);

    @SqlQuery
    (
        "SELECT entity.*, <type>.*, localization_data(entity_id) " +
        "FROM   entity " +
        "       INNER JOIN <type>" +
        "               ON entity.id = <type>.entity_id " +
        "WHERE  entity.type = '<type>' " +
        "       AND entity.tenant_id = :tenant.id " +
        "ORDER  BY <order> ASC "
    )
    List<E> findAll(@Define("type") String type, @Define("order") String order, @BindBean("tenant") Tenant tenant);

    @SqlQuery
    (
        "SELECT entity.*, <type>.*, localization_data(entity_id) " +
        "FROM   entity " +
        "       INNER JOIN <type>" +
        "               ON entity.id = <type>.entity_id " +
        "WHERE  entity.type = '<type>' " +
        "       AND entity.tenant_id = :tenant.id " +
        "LIMIT  :number " +
        "OFFSET :offset "
    )
    List<E> findAll(@Define("type") String type, @BindBean("tenant") Tenant tenant, @Bind("number") Integer number,
            @Bind("offset") Integer offset);


    @SqlQuery
    (
        "SELECT count(*) " +
        "FROM   entity " +
        "       INNER JOIN <type>" +
        "               ON entity.id = <type>.entity_id " +
        "WHERE  entity.type = '<type>' " +
        "       AND entity.tenant_id = :tenant.id "
    )
    Integer countAll(@Define("type") String type, @BindBean("tenant") Tenant tenant);

    @SqlQuery
    (
        "SELECT entity.*, <type>.*, localization_data(entity_id) " +
        "FROM   entity " +
        "       INNER JOIN <type>" +
        "               ON entity.id = <type>.entity_id " +
        "WHERE  entity.id in ( <ids> ) "
    )
    List<E> findByIds(@Define("type") String type, @BindIn("ids") List<UUID> ids);

    @SqlQuery
    (
        "SELECT entity.*, <type>.*, localization_data(entity_id) " +
        "FROM   entity " +
        "       INNER JOIN <type>" +
        "               ON entity.id = <type>.entity_id " +
        "WHERE  entity.type = '<type>' " +
        "       AND entity.tenant_id = :tenant.id " +
        "ORDER  BY <order> ASC " +
        "LIMIT  :number " +
        "OFFSET :offset "
    )
    List<E> findAll(@Define("type") String type, @Define("order") String order, @BindBean("tenant") Tenant tenant,
            @Bind("number") Integer number, @Bind("offset") Integer offset);
}
