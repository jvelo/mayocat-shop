package org.mayocat.store.rdbms.dbi.dao;

import java.util.List;

import org.mayocat.shop.catalog.model.Category;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.jdbi.mapper.CategoryMapper;
import org.mayocat.model.EntityAndCount;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.store.rdbms.dbi.extraction.EntityExtractor;
import org.mayocat.store.rdbms.dbi.jointype.EntityAndCountsJoinRow;
import org.mayocat.store.rdbms.dbi.mapper.EntityAndCountsJoinRowMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import com.google.common.collect.ImmutableList;

@UseStringTemplate3StatementLocator
@RegisterMapper(CategoryMapper.class)
public abstract class CategoryDAO extends AbstractLocalizedEntityDAO<Category> implements Transactional<CategoryDAO>,
        PositionedDAO<Category>
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


    @SqlQuery
    (
        "SELECT * " +
        "FROM   entity " +
        "       INNER JOIN category " +
        "               ON entity.id = category.entity_id " +
        "WHERE  entity.type = 'category' " +
        "       AND category.entity_id IN (SELECT category_id " +
        "                                  FROM   category_product " +
        "                                  WHERE  product_id = :product.id)"
    )
    public abstract List<Category> findAllForProduct(@BindBean("product") Product product);

    @SqlQuery
    (
        "SELECT position " +
        "FROM   category_product " +
        "WHERE  category_id = :category.id " +
        "ORDER  BY position DESC " +
        "LIMIT  1 "
    )
    public abstract Integer lastProductPosition(@BindBean("category") Category category);


    @SqlUpdate
    (
        "INSERT INTO category_product " +
        "            (category_id, " +
        "             product_id, " +
        "             position) " +
        "VALUES      (:category.id, " +
        "             :product.id, " +
        "             :position) "
    )
    public abstract void addProduct(@BindBean("category") Category category, @BindBean("product") Product product,
                                    @Bind("position") Integer position);

    @SqlUpdate
    (
        "DELETE FROM category_product " +
        "WHERE  category_id = :category.id " +
        "       AND product_id = :product.id "
    )
    public abstract void removeProduct(@BindBean("category") Category category, @BindBean("product") Product product);

    @RegisterMapper(EntityAndCountsJoinRowMapper.class)
    @SqlQuery
    (
        "SELECT *, " +
        "       COALESCE(_count_categories.count, 0) AS _count " +
        "FROM   entity " +
        "       INNER JOIN category " +
        "               ON entity.id = category.entity_id " +
        "       LEFT JOIN (SELECT category_product.category_id, " +
        "                         COUNT(category_product.product_id) AS count " +
        "                  FROM   category_product " +
        "                  GROUP  BY category_product.category_id) _count_categories " +
        "              ON _count_categories.category_id = category.entity_id "
    )
    abstract List<EntityAndCountsJoinRow> findWithProductCountRows(@BindBean("tenant") Tenant tenant);

    public Category findBySlug(String slug, Tenant tenant)
    {
        return this.findBySlugWithTranslations("category", slug, tenant);
    }

    public List<EntityAndCount<Category>> findAllWithProductCount(Tenant tenant)
    {
        List<EntityAndCountsJoinRow> rows = this.findWithProductCountRows(tenant);
        ImmutableList.Builder<EntityAndCount<Category>> listBuilder = ImmutableList.builder();
        EntityExtractor<Category> extractor = new EntityExtractor<Category>();
        for (EntityAndCountsJoinRow row : rows) {
            Category c = extractor.extract(row.getEntityData(), Category.class);
            Long count = row.getCounts().get("_count");
            EntityAndCount<Category> entityAndCount = new EntityAndCount<Category>(c, count);
            listBuilder.add(entityAndCount);
        }
        return listBuilder.build();
    }
}
