package mayoapp.dao;

import java.util.List;

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
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import com.google.common.collect.ImmutableList;

@UseStringTemplate3StatementLocator
@RegisterMapper(CollectionMapper.class)
public abstract class CollectionDAO  implements EntityDAO<Collection>, Transactional<CollectionDAO>, PositionedDAO<Collection>
{
    @SqlUpdate
    public abstract void create(@Bind("position") Integer position, @BindBean("collection") Collection collection);

    @SqlUpdate
    public abstract Integer update(@BindBean("collection") Collection collection);

    @SqlQuery
    public abstract Integer lastPosition(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    public abstract List<Collection> findAllForProduct(@BindBean("product") Product product);

    @SqlQuery
    public abstract Integer lastProductPosition(@BindBean("collection") Collection collection);

    @SqlUpdate
    public abstract void addProduct(@BindBean("collection") Collection collection, @BindBean("product") Product product,
            @Bind("position") Integer position);

    @SqlUpdate
    public abstract void removeProduct(@BindBean("collection") Collection collection,
            @BindBean("product") Product product);

    @SqlBatch
    public abstract void updateProductPosition(@BindBean("product") List<Product> entity,
            @Bind("position") List<Integer> position);

    @RegisterMapper(EntityAndCountsJoinRowMapper.class)
    @SqlQuery
    abstract List<EntityAndCountsJoinRow> findWithProductCountRows(@BindBean("tenant") Tenant tenant);

    public Collection findBySlug(String slug, Tenant tenant)
    {
        return this.findBySlug("collection", slug, tenant);
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
