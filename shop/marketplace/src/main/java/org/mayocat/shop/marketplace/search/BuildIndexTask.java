/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.search;

import java.io.PrintWriter;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.store.TenantStore;
import org.mayocat.search.SearchEngine;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.CollectionStore;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;

import mayoapp.dao.CollectionDAO;
import mayoapp.dao.EntityDAO;
import mayoapp.dao.ProductDAO;
import mayoapp.dao.TenantDAO;

@Component("buildMarketplaceIndex")
public class BuildIndexTask extends Task implements org.mayocat.task.Task
{
    @Inject
    private CollectionStore collectionStore;

    @Inject
    private ProductStore productStore;

    @Inject
    private TenantStore tenantStore;

    @Inject
    private Provider<SearchEngine> searchEngine;

    @Inject
    private DBIProvider dbi;

    private EntityDAO<Collection> collectionDAO;

    private EntityDAO<Product> productDAO;

    private TenantDAO tenantDAO;

    public BuildIndexTask()
    {
        this("buildMarketplaceIndex");
    }

    protected BuildIndexTask(String name)
    {
        super(name);
    }

    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception
    {
        this.productDAO = this.dbi.get().onDemand(ProductDAO.class);
        this.tenantDAO = this.dbi.get().onDemand(TenantDAO.class);
        this.collectionDAO = this.dbi.get().onDemand(CollectionDAO.class);

        output.println("(Re)building index...");

        // TODO
        // Design a system where we can re-index all products without loading them all in memory at once.

        for (Tenant t : tenantDAO.findAll("tenant")) {

            // Retrieve addons
            Tenant tenant = tenantStore.findBySlug(t.getSlug());
            searchEngine.get().index(tenant, tenant);
            output.println("- indexing " + t.getSlug());
            output.flush();

            for (Product product : this.productDAO.findAll("product", tenant)) {

                // Retrieve again individually to be sure we have addons etc.
                Product retrieved = this.productStore.findById(product.getId());
                output.println("- indexing " + tenant.getSlug() + ":" + product.getSlug());
                output.flush();

                searchEngine.get().index(retrieved, tenant);
            }

            for (Collection collection : this.collectionDAO.findAll("collection", tenant)) {

                // Retrieve again individually to be sure we have addons etc.
                Collection retrieved = this.collectionStore.findById(collection.getId());
                output.println("- indexing " + tenant.getSlug() + ":" + collection.getSlug());
                output.flush();

                searchEngine.get().index(retrieved, tenant);
            }
        }
    }
}
