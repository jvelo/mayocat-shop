/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.accounts.model.Tenant
import org.mayocat.accounts.store.TenantStore
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.PlatformSettings
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.LoadingOption
import org.mayocat.entity.StandardOptions
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.rest.Resource
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.shop.front.views.ErrorWebView
import org.mayocat.shop.front.views.WebView
import org.mayocat.shop.marketplace.model.EntityAndTenant
import org.mayocat.shop.marketplace.store.MarketplaceProductStore
import org.mayocat.shop.marketplace.web.delegate.WithProductWebObjectBuilder
import org.mayocat.shop.marketplace.web.object.MarketplaceProductWebObject
import org.mayocat.shop.marketplace.web.object.MarketplaceShopWebObject
import org.mayocat.shop.taxes.configuration.TaxesSettings
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.theme.TypeDefinition
import org.mayocat.url.EntityURLFactory
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * @version $Id$
 */
@Component("/marketplace/shops")
@Path("/marketplace/shops")
@Produces([MediaType.TEXT_HTML, MediaType.APPLICATION_JSON])
@CompileStatic
class MarketplaceShopWebView implements Resource, WithProductWebObjectBuilder
{
    @Inject
    Provider<TenantStore> tenantStore

    @Inject
    Provider<MarketplaceProductStore> productStore

    @Inject
    Provider<ProductStore> tenantProductStore

    @Inject
    EntityDataLoader dataLoader

    @Inject
    PlatformSettings platformSettings

    @Inject
    EntityURLFactory urlFactory

    @Inject
    ThemeFileResolver themeFileResolver

    @GET
    def listShops()
    {
        def context = [:]

        List<Tenant> tenants = tenantStore.get().findAll(100, 0)
        List<MarketplaceShopWebObject> shops = []

        List<EntityData<Tenant>> tenantsData = dataLoader.
                load(tenants, StandardOptions.LOCALIZE, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY)

        tenantsData.each({ EntityData<Tenant> tenantData ->
            Tenant tenant = tenantData.entity
            MarketplaceShopWebObject shopWebObject = new MarketplaceShopWebObject().withTenant(tenant)
            shopWebObject.withAddons(addonsWebObjectBuilder.build(tenantData))

            Optional<ImageGallery> gallery = tenantData.getData(ImageGallery.class)
            List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

            shopWebObject.withImages(tenant, images, tenant.featuredImageId, platformSettings)
            shops << shopWebObject
        })

        context.put("shops", shops)

        return new WebView().data(context)
    }

    @GET
    @Path("{shop}")
    def getShop(@PathParam("shop") String shopSlug)
    {
        def context = [:]

        Tenant tenant = tenantStore.get().findBySlug(shopSlug)

        if (!tenant) {
            return new ErrorWebView().status(404)
        }

        // Shop data
        EntityData<Tenant> tenantData = dataLoader.load(tenant)
        MarketplaceShopWebObject shopWebObject = new MarketplaceShopWebObject().withTenant(tenant)
        shopWebObject.withAddons(addonsWebObjectBuilder.build(tenantData))
        Optional<ImageGallery> gallery = tenantData.getData(ImageGallery.class)
        List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>
        shopWebObject.withImages(tenant, images, tenant.featuredImageId, platformSettings)
        context.put("shop", shopWebObject)

        // Products
        List<Product> products = productStore.get().findAllForTenantOnShelf(tenant, 100, 0);

        List<EntityData<Product>> productsData = dataLoader.
                load(products, StandardOptions.LOCALIZE, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY)

        List<MarketplaceProductWebObject> productWebObjects = []

        productsData.each({ EntityData<Product> entityData ->
            MarketplaceProductWebObject productWebObject = buildProductWebObject(tenant, entityData)

            productWebObjects << productWebObject
        })

        shopWebObject.products = productWebObjects

        return new WebView().data(context)
    }

    @GET
    @Path("{shop}/products/{product}")
    def getShopProduct(@PathParam("shop") String shopSlug, @PathParam("product") String productSlug)
    {
        def context = [:]

        Product product = this.productStore.get().findBySlugAndTenant(productSlug, shopSlug)

        if (!product) {
            return new ErrorWebView().status(404)
        }

        // Product
        def productData = dataLoader.load(product, StandardOptions.LOCALIZE)
        def tenant = productData.getData(Tenant.class).orNull()
        MarketplaceProductWebObject productWebObject = buildProductWebObject(tenant, productData)

        // Shop data
        EntityData<Tenant> tenantData = dataLoader.load(tenant, StandardOptions.LOCALIZE)
        MarketplaceShopWebObject shopWebObject = new MarketplaceShopWebObject().withTenant(tenant)
        shopWebObject.withAddons(addonsWebObjectBuilder.build(tenantData))
        Optional<ImageGallery> gallery = tenantData.getData(ImageGallery.class)
        List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>
        shopWebObject.withImages(tenant, images, tenant.featuredImageId, platformSettings)

        context.put("shop", shopWebObject)
        productWebObject.shop = shopWebObject
        context.put("product", productWebObject)

        return new WebView().data(context)
    }

}
