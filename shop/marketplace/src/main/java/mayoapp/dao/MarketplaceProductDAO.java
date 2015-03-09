/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.List;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.jdbi.mapper.ProductMapper;
import org.mayocat.shop.marketplace.store.jdbi.ProductAndTenantMapper;
import org.mayocat.store.rdbms.dbi.argument.MapAsJsonArgumentFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterArgumentFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@RegisterMapper(ProductMapper.class)
@RegisterArgumentFactory({ MapAsJsonArgumentFactory.class })
@UseStringTemplate3StatementLocator
public abstract class MarketplaceProductDAO implements AddonsDAO<Product>
{
    @SqlQuery
    public abstract Product findBySlugAndTenant(@Bind("slug") String slug,
            @Bind("tenantSlug") String tenantSlug);

    @SqlQuery
    public abstract List<Product> findAllNotVariants(@Bind("number") Integer number,
            @Bind("offset") Integer offset, @Define("orderby") String orderBy);

    @SqlQuery
    public abstract Integer countAllNotVariants();

    @SqlQuery
    public abstract List<Product> findAllWithTitleLike(@Bind("title") String title,
            @Bind("number") Integer number,
            @Bind("offset") Integer offset);

    @SqlQuery
    public abstract List<Product> findAllOnShelfWithTitleLike(@Bind("title") String title,
            @Bind("number") Integer number,
            @Bind("offset") Integer offset);

    @SqlQuery
    public abstract Integer countAllWithTitleLike(@Bind("title") String title);

    @SqlQuery
    public abstract Integer countAllOnShelfWithTitleLike(@Bind("title") String title);

    @SqlQuery
    public abstract List<Product> findAllForTenant(
            @BindBean("tenant") Tenant tenant,
            @Bind("number") Integer number,
            @Bind("offset") Integer offset);

    @SqlQuery
    public abstract List<Product> findAllForTenantOnShelf(
            @BindBean("tenant") Tenant tenant,
            @Bind("number") Integer number,
            @Bind("offset") Integer offset
    );
}
