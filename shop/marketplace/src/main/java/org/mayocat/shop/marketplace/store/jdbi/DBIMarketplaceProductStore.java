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
import org.mayocat.addons.store.dbi.AddonsHelper;
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
import mayoapp.dao.TenantDAO;

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

    private TenantDAO tenantDAO;

    @Override
    public Product findBySlugAndTenant(String slug, String tenantSlug)
    {
        Product product = this.marketplaceProductDAO.findBySlugAndTenant(slug, tenantSlug);
        if (product != null) {
            List<AddonGroup> addons = this.marketplaceProductDAO.findAddons(product);
            product.setAddons(asMap(addons));
        }
        return product;
    }

    @Override
    public List<Product> findAllNotVariants(Integer number, Integer offset)
    {
        return AddonsHelper
                .withAddons(this.marketplaceProductDAO.findAllNotVariants(number, offset, "product.title"),
                        this.marketplaceProductDAO);
    }

    @Override
    public List<Product> findAllNotVariants(Integer number, Integer offset, Order order)
    {
        String orderby = "product.title";
        switch (order) {
            case TENANT_NAME_THEN_PRODUCT_TITLE:
                orderby = "tenant.name, product.title";
        }
        return AddonsHelper
                .withAddons(this.marketplaceProductDAO.findAllNotVariants(number, offset, orderby),
                        this.marketplaceProductDAO);
    }

    @Override
    public Integer countAllNotVariants()
    {
        return this.marketplaceProductDAO.countAllNotVariants();
    }

    @Override
    public List<Product> findAllWithTitleLike(String title, Integer number, Integer offset)
    {
        return AddonsHelper
                .withAddons(this.marketplaceProductDAO.findAllWithTitleLike(title, number, offset),
                        this.marketplaceProductDAO);
    }

    @Override
    public Integer countAllWithTitleLike(String title)
    {
        return this.marketplaceProductDAO.countAllWithTitleLike(title);
    }

    @Override
    public List<Product> findAllOnShelfWithTitleLike(String title, Integer number, Integer offset)
    {
        return AddonsHelper
                .withAddons(this.marketplaceProductDAO.findAllOnShelfWithTitleLike(title, number, offset),
                        this.marketplaceProductDAO);
    }

    @Override
    public Integer countAllOnShelfWithTitleLike(String title)
    {
        return this.marketplaceProductDAO.countAllOnShelfWithTitleLike(title);
    }

    @Override
    public List<Product> findAllForTenant(Tenant tenant, Integer number, Integer offset)
    {
        return AddonsHelper.withAddons(this.marketplaceProductDAO.findAllForTenant(tenant, number, offset),
                this.marketplaceProductDAO);
    }

    @Override
    public List<Product> findAllForTenantOnShelf(Tenant tenant, Integer number, Integer offset)
    {
        return AddonsHelper.withAddons(this.marketplaceProductDAO.findAllForTenantOnShelf(tenant, number, offset),
                this.marketplaceProductDAO);
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.marketplaceProductDAO = this.dbi.get().onDemand(MarketplaceProductDAO.class);
        this.tenantDAO = this.dbi.get().onDemand(TenantDAO.class);
    }
}
