/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web.delegate

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.accounts.model.Tenant
import org.mayocat.addons.web.AddonsWebObjectBuilder
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.PlatformSettings
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.entity.EntityData
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.shop.marketplace.web.object.MarketplaceProductWebObject
import org.mayocat.shop.marketplace.web.object.MarketplaceShopWebObject
import org.mayocat.shop.taxes.configuration.TaxesSettings
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.theme.TypeDefinition
import org.mayocat.url.EntityURLFactory

import javax.inject.Inject
import javax.inject.Provider

/**
 * @version $Id$
 */
@CompileStatic
trait WithProductWebObjectBuilder
{
    @Inject
    ConfigurationService configurationService

    @Inject
    Provider<ProductStore> tenantProductStore

    @Inject
    EntityURLFactory urlFactory

    @Inject
    ThemeFileResolver themeFileResolver

    @Inject
    PlatformSettings platformSettings

    @Inject
    AddonsWebObjectBuilder addonsWebObjectBuilder

    MarketplaceProductWebObject buildProductWebObject(Tenant tenant, EntityData<Product> entityData)
    {
        def catalogSettings = configurationService.getSettings(CatalogSettings.class)
        def generalSettings = configurationService.getSettings(GeneralSettings.class)
        def taxesSettings = configurationService.getSettings(TaxesSettings.class)

        def product = entityData.entity
        Optional<ImageGallery> productGallery = entityData.getData(ImageGallery.class);
        List<Image> productImages = productGallery.isPresent() ? productGallery.get().images : [] as List<Image>

        def productWebObject = new MarketplaceProductWebObject()
        productWebObject.withProduct(entityData.entity, urlFactory,
                themeFileResolver, catalogSettings, generalSettings, taxesSettings)
        productWebObject.withImages(tenant, productImages, entityData.entity.featuredImageId, platformSettings)
        if (product.addons.isLoaded()) {
            productWebObject.withAddons(addonsWebObjectBuilder.build(entityData))
        }

        if (product.virtual) {
            def features = tenantProductStore.get().findFeatures(product)
            def variants = tenantProductStore.get().findVariants(product)

            Map<String, TypeDefinition> types = [:]
            types.putAll(catalogSettings.productsSettings.types)

            productWebObject.withFeaturesAndVariants(features, variants, [:],
                    catalogSettings, generalSettings, taxesSettings, types)
        }

        productWebObject.shop = new MarketplaceShopWebObject().withTenant(tenant)

        return productWebObject
    }
}
