package org.mayocat.shop.store.rdbms.dbi.dao;

import java.util.List;

import javax.ws.rs.core.Variant;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.EntityAndCount;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.rdbms.dbi.extraction.EntityExtractor;
import org.mayocat.shop.store.rdbms.dbi.jointype.EntityAndCountsJoinRow;
import org.mayocat.shop.store.rdbms.dbi.mapper.CategoryMapper;
import org.mayocat.shop.store.rdbms.dbi.mapper.EntityAndCountsJoinRowMapper;
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
        "       COUNT(product_id) " +
        "FROM   entity " +
        "       INNER JOIN category " +
        "               ON entity.id = category.entity_id " +
        "       RIGHT JOIN category_product " +
        "               ON category_product.category_id = category.entity_id " +
        "WHERE  entity.tenant_id = :tenant.id " +
        "GROUP  BY entity.slug " +
        "ORDER  BY title ASC "
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
            Long count = row.getCounts().get("product_id");
            EntityAndCount<Category> entityAndCount = new EntityAndCount<Category>(c, count);
            listBuilder.add(entityAndCount);
        }
        return listBuilder.build();
    }
}
