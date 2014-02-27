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
    public abstract Integer updatePosition(@Bind("position") Integer position, @BindBean("product") Product product);

    @SqlUpdate
    public abstract Integer deleteProductFromCollections(@Bind("id") UUID id);

    @SqlQuery
    public abstract Integer lastPosition(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    public abstract Integer lastPositionForVariant(@BindBean("product") Product parent);

    @SqlQuery
    public abstract List<Product> findOrphanProducts(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    public abstract List<Product> findForCollection(@BindBean("collection") Collection collection,
            @Bind("number") Integer number, @Bind("offset") Integer offset);

    @SqlQuery
    public abstract List<Product> findAllForCollection(@BindBean("collection") Collection collection);

    @SqlQuery
    public abstract Integer countAllForCollection(@BindBean("collection") Collection collection);

    @SqlQuery
    public abstract List<Product> findAllOnShelf(@BindBean("tenant") Tenant tenant, @Bind("number") Integer number,
            @Bind("offset") Integer offset);

    @SqlQuery
    public abstract Integer countAllOnShelf(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    public abstract List<Product> findAllNotVariants(@BindBean("tenant") Tenant tenant, @Bind("number") Integer number,
            @Bind("offset") Integer offset);

    @SqlQuery
    public abstract Integer countAllNotVariants(@BindBean("tenant") Tenant tenant);

    @SqlQuery
    public abstract List<Product> findAllWithTitleLike(@BindBean("tenant") Tenant tenant, @Bind("title") String title,
            @Bind("number") Integer number, @Bind("offset") Integer offset);

    @SqlQuery
    public abstract Integer countAllWithTitleLike(@BindBean("tenant") Tenant tenant, @Bind("title") String title);

    @SqlQuery
    public abstract List<Product> findAllVariants(@BindBean("product") Product product);

    public Product findBySlug(String slug, Tenant tenant)
    {
        return this.findBySlug("product", slug, tenant);
    }

    public Product findBySlug(String slug, Tenant tenant, UUID parent)
    {
        return this.findBySlug("product", slug, tenant, parent);
    }

    public void createOrUpdateAddons(Product entity)
    {
        AddonsHelper.createOrUpdateAddons(this, entity);
    }
}
