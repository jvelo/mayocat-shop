package mayoapp.dao;

import java.util.List;
import java.util.UUID;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.addons.store.dbi.AddonsHelper;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.jdbi.mapper.ProductMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

@RegisterMapper(ProductMapper.class)
@UseStringTemplate3StatementLocator
public abstract class ProductDAO implements EntityDAO<Product>, Transactional<ProductDAO>,
        PositionedDAO<Product>, AddonsDAO<Product>, LocalizationDAO<Product>
{
    @SqlUpdate
    public abstract void createProduct(@Bind("position") Integer position, @BindBean("product") Product product);

    @SqlUpdate
    public abstract Integer updateProduct(@BindBean("product") Product product);

    @SqlUpdate
    public abstract Integer deleteProductFromCollections(@Bind("id") UUID id);

    @SqlQuery
    public abstract Integer lastPosition(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    public abstract List<Product> findOrphanProducts(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    public abstract List<Product> findAllForCollection(@BindBean("collection") Collection collection);

    @SqlQuery
    public abstract List<Product> findAllOnShelf(@BindBean("tenant") Tenant tenant, @Bind("number") Integer number,
            @Bind("offset") Integer offset);

    public Product findBySlug(String slug, Tenant tenant)
    {
        return this.findBySlug("product", slug, tenant);
    }

    public void createOrUpdateAddons(Product entity)
    {
        AddonsHelper.createOrUpdateAddons(this, entity);
    }

}
