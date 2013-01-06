package org.mayocat.shop.store.rdbms.dbi.dao;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.rdbms.dbi.mapper.CategoryMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
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
        "INSERT INTO category (entity_id, title) VALUES (:id, :category.title)"
    )
    public abstract void create(@Bind("id") Long entityId, @BindBean("category") Category category);
    
    @SqlUpdate
    (
        "UPDATE category SET title=:category.title, password=:u.password WHERE id=:category.id"
    )
    public abstract void update(@BindBean("category") Category category);

    public Object findBySlug(String slug, Tenant tenant)
    {
        return this.findBySlugWithTranslations("category", slug, tenant);
    }
}
