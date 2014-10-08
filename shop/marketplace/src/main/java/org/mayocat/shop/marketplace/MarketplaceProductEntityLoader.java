package org.mayocat.shop.marketplace;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.entity.EntityLoader;
import org.mayocat.model.Entity;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.marketplace.store.MarketplaceProductStore;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 *
 * TODO: merge product stores and have this in the catalog module
 */
@Component("product")
public class MarketplaceProductEntityLoader implements EntityLoader
{
    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private Provider<MarketplaceProductStore> marketplaceProductStore;

    public <E extends Entity> E load(String slug)
    {
        return (E) productStore.get().findBySlug(slug);
    }

    public <E extends Entity> E load(String slug, String tenantSlug)
    {
        return (E) marketplaceProductStore.get().findBySlugAndTenant(slug, tenantSlug).getEntity();
    }
}

