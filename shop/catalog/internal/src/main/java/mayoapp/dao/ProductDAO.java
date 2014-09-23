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
import org.mayocat.addons.store.dbi.AddonsHelper;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.jdbi.mapper.ProductMapper;
import org.mayocat.store.rdbms.dbi.argument.MapAsJsonArgumentFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterArgumentFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

@RegisterMapper(ProductMapper.class)
@RegisterArgumentFactory({ MapAsJsonArgumentFactory.class })
@UseStringTemplate3StatementLocator
public abstract class ProductDAO implements EntityDAO<Product>, Transactional<ProductDAO>,
        PositionedDAO<Product>, AddonsDAO<Product>, LocalizationDAO<Product>
{
    @SqlUpdate
    public abstract void createProduct(@Bind("position") Integer position, @BindBean("product") Product product);

    @SqlUpdate
    public abstract Integer updateProduct(@BindBean("product") Product product);

    @SqlUpdate
    public abstract Integer updatePosition(@Bind("position") Integer position, @BindBean("product") Product product);

    @SqlUpdate
    public abstract Integer deleteProductFromCollections(@Bind("id") UUID id);

    @SqlQuery
    public abstract Integer lastPosition(@Bind("tenantId") UUID tenant);

    @SqlQuery
    public abstract Integer lastPositionForVariant(@BindBean("product") Product parent);

    @SqlQuery
    public abstract List<Product> findOrphanProducts(@Bind("tenantId") UUID tenant);

    @SqlQuery
    public abstract List<Product> findForCollection(@BindBean("collection") Collection collection,
            @Bind("number") Integer number, @Bind("offset") Integer offset);

    @SqlQuery
    public abstract List<Product> findAllForCollection(@BindBean("collection") Collection collection);

    @SqlQuery
    public abstract List<Product> findAllForCollectionPath(@Bind("path") String path);

    @SqlQuery
    public abstract Integer countAllForCollection(@BindBean("collection") Collection collection);

    @SqlQuery
    public abstract List<Product> findAllOnShelf(@Bind("tenantId") UUID tenant, @Bind("number") Integer number,
            @Bind("offset") Integer offset);

    @SqlQuery
    public abstract Integer countAllOnShelf(@Bind("tenantId") UUID tenant);

    @SqlQuery
    public abstract List<Product> findAllNotVariants(@Bind("tenantId") UUID tenant, @Bind("number") Integer number,
            @Bind("offset") Integer offset);

    @SqlQuery
    public abstract Integer countAllNotVariants(@Bind("tenantId") UUID tenant);

    @SqlQuery
    public abstract List<Product> findAllWithTitleLike(@Bind("tenantId") UUID tenant, @Bind("title") String title,
            @Bind("number") Integer number, @Bind("offset") Integer offset);

    @SqlQuery
    public abstract Integer countAllWithTitleLike(@Bind("tenantId") UUID tenant, @Bind("title") String title);

    @SqlQuery
    public abstract List<Product> findAllVariants(@BindBean("product") Product product);

    public Product findBySlug(String slug, UUID tenantId)
    {
        return this.findBySlug("product", slug, tenantId);
    }

    public Product findBySlug(String slug, UUID tenantId, UUID parent)
    {
        return this.findBySlug("product", slug, tenantId, parent);
    }

    public void createOrUpdateAddons(Product entity)
    {
        AddonsHelper.createOrUpdateAddons(this, entity);
    }
}
