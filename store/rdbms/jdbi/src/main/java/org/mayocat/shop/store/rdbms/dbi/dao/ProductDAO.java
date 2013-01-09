package org.mayocat.shop.store.rdbms.dbi.dao;

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
public abstract class ProductDAO extends AbstractLocalizedEntityDAO<Product> implements Transactional<ProductDAO>
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
        "WHERE  id = :product.id "
    )
    public abstract void update(@BindBean("product") Product product);
    
    @SqlQuery
    (
        "SELECT product.position FROM entity INNER JOIN product ON entity.id = product.entity_id " +
        "WHERE entity.type = 'product' AND entity.tenant_id = :tenant.id ORDER BY position DESC LIMIT 1 "
    )
    public abstract Integer lastPosition(@BindBean("tenant") Tenant tenant);

    public Object findBySlug(String slug, Tenant tenant)
    {
        return this.findBySlugWithTranslations("product", slug, tenant);
    }
}