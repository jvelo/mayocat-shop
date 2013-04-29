package org.mayocat.store.rdbms.dbi.dao;

import java.util.List;
import java.util.UUID;

import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.jdbi.mapper.CollectionMapper;
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
@RegisterMapper(CollectionMapper.class)
public abstract class CollectionDAO extends AbstractLocalizedEntityDAO<Collection>
        implements Transactional<CollectionDAO>, PositionedDAO<Collection>
{

    @SqlUpdate
    (
        "INSERT INTO collection " +
        "            (entity_id, " +
        "             position, " +
        "             title, " +
        "             description) " +
        "VALUES      (:id, " +
        "             :position, " +
        "             :collection.title, " +
        "             :collection.description) "
    )
    public abstract void create(@Bind("id") UUID entityId, @Bind("position") Integer position,
            @BindBean("collection") Collection collection);

    @SqlUpdate
    (
        "UPDATE collection " +
        "SET    title = :collection.title, " +
        "       description = :collection.description " +
        "WHERE  entity_id = :collection.id "
    )
    public abstract Integer update(@BindBean("collection") Collection collection);

    @SqlQuery
    (
        "SELECT collection.position " +
        "FROM   entity " +
        "       INNER JOIN collection " +
        "               ON entity.id = collection.entity_id " +
        "WHERE  entity.type = 'collection' " +
        "       AND entity.tenant_id = :tenant.id " +
        "ORDER  BY position DESC " +
        "LIMIT  1"
    )
    public abstract Integer lastPosition(@BindBean("tenant") Tenant tenant);


    @SqlQuery
    (
        "SELECT * " +
        "FROM   entity " +
        "       INNER JOIN collection " +
        "               ON entity.id = collection.entity_id " +
        "WHERE  entity.type = 'collection' " +
        "       AND collection.entity_id IN (SELECT collection_id " +
        "                                  FROM   collection_product " +
        "                                  WHERE  product_id = :product.id)"
    )
    public abstract List<Collection> findAllForProduct(@BindBean("product") Product product);

    @SqlQuery
    (
        "SELECT position " +
        "FROM   collection_product " +
        "WHERE  collection_id = :collection.id " +
        "ORDER  BY position DESC " +
        "LIMIT  1 "
    )
    public abstract Integer lastProductPosition(@BindBean("collection") Collection collection);


    @SqlUpdate
    (
        "INSERT INTO collection_product " +
        "            (collection_id, " +
        "             product_id, " +
        "             position) " +
        "VALUES      (:collection.id, " +
        "             :product.id, " +
        "             :position) "
    )
    public abstract void addProduct(@BindBean("collection") Collection collection, @BindBean("product") Product product,
                                    @Bind("position") Integer position);

    @SqlUpdate
    (
        "DELETE FROM collection_product " +
        "WHERE  collection_id = :collection.id " +
        "       AND product_id = :product.id "
    )
    public abstract void removeProduct(@BindBean("collection") Collection collection, @BindBean("product") Product product);

    @RegisterMapper(EntityAndCountsJoinRowMapper.class)
    @SqlQuery
    (
        "SELECT *, " +
        "       COALESCE(_count_collections.count, 0) AS _count " +
        "FROM   entity " +
        "       INNER JOIN collection " +
        "               ON entity.id = collection.entity_id " +
        "       LEFT JOIN (SELECT collection_product.collection_id, " +
        "                         COUNT(collection_product.product_id) AS count " +
        "                  FROM   collection_product " +
        "                  GROUP  BY collection_product.collection_id) _count_collections " +
        "              ON _count_collections.collection_id = collection.entity_id " +
        "WHERE  entity.tenant_id = :tenant.id"
    )
    abstract List<EntityAndCountsJoinRow> findWithProductCountRows(@BindBean("tenant") Tenant tenant);

    public Collection findBySlug(String slug, Tenant tenant)
    {
        return this.findBySlugWithTranslations("collection", slug, tenant);
    }

    public List<EntityAndCount<Collection>> findAllWithProductCount(Tenant tenant)
    {
        List<EntityAndCountsJoinRow> rows = this.findWithProductCountRows(tenant);
        ImmutableList.Builder<EntityAndCount<Collection>> listBuilder = ImmutableList.builder();
        EntityExtractor<Collection> extractor = new EntityExtractor<Collection>();
        for (EntityAndCountsJoinRow row : rows) {
            Collection c = extractor.extract(row.getEntityData(), Collection.class);
            Long count = row.getCounts().get("_count");
            EntityAndCount<Collection> entityAndCount = new EntityAndCount<Collection>(c, count);
            listBuilder.add(entityAndCount);
        }
        return listBuilder.build();
    }
}
