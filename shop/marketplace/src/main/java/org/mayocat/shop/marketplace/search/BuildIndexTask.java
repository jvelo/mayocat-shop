package org.mayocat.shop.marketplace.search;

import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.search.SearchEngine;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;

import mayoapp.dao.EntityDAO;
import mayoapp.dao.ProductDAO;
import mayoapp.dao.TenantDAO;

@Component("buildMarketplaceIndex")
public class BuildIndexTask extends Task implements org.mayocat.task.Task
{
    @Inject
    private ProductStore productStore;

    @Inject
    private Provider<SearchEngine> searchEngine;

    @Inject
    private DBIProvider dbi;

    private EntityDAO<Product> dao;

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
        this.dao = this.dbi.get().onDemand(ProductDAO.class);
        this.tenantDAO = this.dbi.get().onDemand(TenantDAO.class);

        output.println("(Re)building index...");


        // TODO
        // Design a system where we can re-index all products without loading them all in memory at once.

        for (Tenant tenant : tenantDAO.findAll("tenant")) {

            searchEngine.get().index(tenant, tenant);
            output.println("- indexing " + tenant.getSlug());
            output.flush();

            for (Product product : this.dao.findAll("product", tenant)) {

                // Retrieve again individually to be sure we have addons etc.
                Product retrieved = this.productStore.findById(product.getId());
                output.println("- indexing " + tenant.getSlug() + ":" + product.getSlug());
                output.flush();

                searchEngine.get().index(retrieved, tenant);
            }
        }
    }
}
