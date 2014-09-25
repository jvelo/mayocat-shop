/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.store.jdbi;

import java.util.List;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.context.WebContext;
import org.mayocat.model.AddonGroup;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.marketplace.model.EntityAndTenant;
import org.mayocat.shop.marketplace.store.MarketplaceProductStore;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import mayoapp.dao.MarketplaceProductDAO;

import static org.mayocat.addons.util.AddonUtils.asMap;

/**
 * @version $Id$
 */
@Component(hints = { "jdbi", "default" })
public class DBIMarketplaceProductStore implements MarketplaceProductStore, Initializable
{
    @Inject
    private WebContext context;

    @Inject
    private DBIProvider dbi;

    private MarketplaceProductDAO marketplaceProductDAO;

    @Override
    public EntityAndTenant<Product> findBySlugAndTenant(String slug, String tenantSlug)
    {
        EntityAndTenant<Product> result = this.marketplaceProductDAO.findBySlugAndTenant(slug, tenantSlug);
        if (result != null) {
            List<AddonGroup> addons = this.marketplaceProductDAO.findAddons(result.getEntity());
            result.getEntity().setAddons(asMap(addons));
        }
        return result;
    }

    @Override
    public List<EntityAndTenant<Product>> findAllNotVariants(Integer number, Integer offset)
    {
        return this.marketplaceProductDAO.findAllNotVariants(number, offset);
    }

    @Override
    public Integer countAllNotVariants()
    {
        return this.marketplaceProductDAO.countAllNotVariants();
    }

    @Override
    public List<EntityAndTenant<Product>> findAllWithTitleLike(String title, Integer number, Integer offset)
    {
        return this.marketplaceProductDAO.findAllWithTitleLike(title, number, offset);
    }

    @Override
    public Integer countAllWithTitleLike(String title)
    {
        return this.marketplaceProductDAO.countAllWithTitleLike(title);
    }

    @Override
    public List<EntityAndTenant<Product>> findAllForTenant(Tenant tenant, Integer number, Integer offset)
    {
        return this.marketplaceProductDAO.findAllForTenant(tenant, number, offset);
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.marketplaceProductDAO = this.dbi.get().onDemand(MarketplaceProductDAO.class);
    }
}
