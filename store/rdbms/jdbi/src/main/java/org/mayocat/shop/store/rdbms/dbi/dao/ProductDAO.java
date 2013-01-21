package org.mayocat.shop.store.rdbms.dbi.dao;

import java.util.List;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.rdbms.dbi.mapper.ProductMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

@RegisterMapper(ProductMapper.class)
@UseStringTemplate3StatementLocator
public abstract class ProductDAO extends AbstractLocalizedEntityDAO<Product> implements Transactional<ProductDAO>,
        PositionedDAO<Product>
{
    @SqlUpdate
    (
        "INSERT INTO product " +
        "            (entity_id, " +
        "             position, " +
        "             title, " +
        "             description) " +
        "VALUES      (:id, " +
        "             :position, " +
        "             :product.title, " +
        "             :product.description) "
    )
    public abstract void create(@Bind("id") Long entityId, @Bind("position") Integer position, @BindBean("product") Product product);
    
    @SqlUpdate
    (
        "UPDATE product " +
        "SET    title = :product.title, " +
        "       description = :product.description " +
        "WHERE  entity_id = :product.id "
    )
    public abstract Integer update(@BindBean("product") Product product);
    
    @SqlQuery
    (
        "SELECT product.position FROM entity INNER JOIN product ON entity.id = product.entity_id " +
        "WHERE entity.type = 'product' AND entity.tenant_id = :tenant.id ORDER BY position DESC LIMIT 1 "
    )
    public abstract Integer lastPosition(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    (
        "SELECT * " +
        "FROM   entity " +
        "       INNER JOIN product " +
        "               ON product.entity_id = entity.id " +
        "WHERE  NOT EXISTS (SELECT product_id " +
        "                   FROM   category_product " +
        "                   WHERE  product_id = entity.id) " +
        "       AND entity.tenant_id = 1 " +
        "ORDER  BY product.position ASC "
    )
    public abstract List<Product> findUncategorized(@BindBean("tenant")Tenant tenant);

    @SqlQuery
    (
        "SELECT * " +
        "FROM   category_product " +
        "       INNER JOIN entity " +
        "               ON entity.id = category_product.product_id " +
        "       INNER JOIN product " +
        "               ON product.entity_id = category_product.product_id " +
        "WHERE  category_product.category_id = :category.id " +
        "ORDER  BY category_product.position "
    )
    public abstract List<Product> findAllForCategory(@BindBean("category")Category category);

    public Product findBySlug(String slug, Tenant tenant)
    {
        return this.findBySlugWithTranslations("product", slug, tenant);
    }


}