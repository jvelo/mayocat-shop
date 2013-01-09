package org.mayocat.shop.store.rdbms.dbi.dao;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.rdbms.dbi.mapper.CategoryMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

@UseStringTemplate3StatementLocator
@RegisterMapper(CategoryMapper.class)
public abstract class CategoryDAO extends AbstractLocalizedEntityDAO<Category> implements Transactional<CategoryDAO>
{

    @SqlUpdate
    (
        "INSERT INTO category " +
        "            (entity_id, " +
        "             position, " +
        "             title, " +
        "             description) " +
        "VALUES      (:id, " +
        "             :position, " +
        "             :category.title, " +
        "             :category.description) "
    )
    public abstract void create(@Bind("id") Long entityId, @Bind("position") Integer position,
            @BindBean("category") Category category);
    
    @SqlUpdate
    (
        "UPDATE category " +
        "SET    title = :category.title, " +
        "       description = :category.description " +
        "WHERE  entity_id = :category.id "
    )
    public abstract Integer update(@BindBean("category") Category category);

    @SqlQuery
    (
        "SELECT category.position " +
        "FROM   entity " +
        "       INNER JOIN category " +
        "               ON entity.id = category.entity_id " +
        "WHERE  entity.type = 'category' " +
        "       AND entity.tenant_id = :tenant.id " +
        "ORDER  BY position DESC " +
        "LIMIT  1"
    )
    public abstract Integer lastPosition(@BindBean("tenant") Tenant tenant);

    public Category findBySlug(String slug, Tenant tenant)
    {
        return this.findBySlugWithTranslations("category", slug, tenant);
    }
}
