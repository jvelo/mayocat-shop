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
import org.mayocat.cms.home.model.HomePage
import org.mayocat.cms.home.store.HomePageStore
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.PlatformSettings
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.StandardOptions
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.model.EntityList
import org.mayocat.rest.Resource
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.shop.front.views.WebView
import org.mayocat.shop.marketplace.web.delegate.WithProductWebObjectBuilder
import org.mayocat.shop.marketplace.web.object.MarketplaceHomeWebObject
import org.mayocat.shop.marketplace.web.object.MarketplaceProductWebObject
import org.mayocat.shop.marketplace.web.object.MarketplaceShopWebObject
import org.mayocat.store.EntityListStore
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.url.EntityURLFactory
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * @version $Id$
 */
@Component("/marketplace")
@Path("/marketplace")
@Produces([MediaType.TEXT_HTML, MediaType.APPLICATION_JSON])
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@CompileStatic
class MarketplaceHomeWebView implements Resource, WithProductWebObjectBuilder
{
    @Inject
    Provider<HomePageStore> homePageStore

    @Inject
    Provider<ProductStore> productStore

    @Inject
    Provider<TenantStore> tenantStore

    @Inject
    Provider<EntityListStore> entityListStore

    @Inject
    EntityDataLoader dataLoader

    @Inject
    ConfigurationService configurationService

    @Inject
    PlatformSettings platformSettings

    @Inject
    ThemeFileResolver themeFileResolver

    @Inject
    EntityURLFactory urlFactory

    @Inject
    WebContext context

    @GET
    def getHomePage()
    {

        // Home Page data

        MarketplaceHomeWebObject homeWebObject = new MarketplaceHomeWebObject()
        HomePage homePage = homePageStore.get().getOrCreate(new HomePage())
        EntityData<HomePage> homeData = dataLoader.load(homePage, StandardOptions.LOCALIZE)
        homeWebObject.addons = addonsWebObjectBuilder.build(homeData)

        // Featured products

        def List<EntityList> lists = entityListStore.get().findListsByHint("home_featured_products");
        if (!lists.isEmpty() && !lists.first().entities.isEmpty()) {
            List<Product> products = productStore.get().findByIds(lists.first().entities)
            List<Product> sorted = lists.first().entities.findAll({ UUID id -> id != null }).collect({ UUID id ->
                products.find({ Product product -> product.id == id })
            }) as List<Product>;

            List<EntityData<Product>> productsData = dataLoader.
                    load(sorted, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY, StandardOptions.LOCALIZE)

            List<MarketplaceProductWebObject> list = []

            productsData.each({ EntityData<Product> productData ->
                Product product = productData.entity
                def tenant = tenantStore.get().findById(product.tenantId)
                MarketplaceProductWebObject productWebObject = buildProductWebObject(tenant, productData)
                list << productWebObject
            })

            homeWebObject.featuredProducts = list
        }

        // Featured tenants

        def List<EntityList> tenantLists = entityListStore.get().findListsByHint("home_featured_tenants");
        if (tenantLists.isEmpty() || tenantLists.first().entities.isEmpty()) {
            homeWebObject.featuredShops = []
        } else {
            def List<Tenant> tenants = tenantStore.get().findByIds(tenantLists.first().entities);

            def List<EntityData> tenantsData = dataLoader.load(tenants,
                    StandardOptions.LOCALIZE, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY);

            List<MarketplaceShopWebObject> shops = [];

            tenantLists.first().entities.each({ UUID tenantId ->

                /*
                def EntityData<Tenant> data
                tenantsData.each({ EntityData<Tenant> tenantData ->
                    if (tenantData.entity.id == tenantId) {
                        data = tenantData
                    }
                })
                */

                // I don't know why the groovy compiler won't compile the line below instead :/
                // It fails with :
                // [Static type checking] - Cannot assign value of type org.mayocat.accounts.model.Tenant
                // to variable of type org.mayocat.entity.EntityData <Tenant>

                def EntityData<Tenant> data = tenantsData.
                        find({ EntityData<Tenant> tenantData -> tenantData.entity.id == tenantId })

                if (data) {
                    Tenant tenant = data.entity
                    MarketplaceShopWebObject shopWebObject = new MarketplaceShopWebObject().withTenant(tenant)
                    shopWebObject.withAddons(addonsWebObjectBuilder.build(data))

                    Optional<ImageGallery> gallery = data.getData(ImageGallery.class)
                    List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

                    shopWebObject.withImages(tenant, images, tenant.featuredImageId, platformSettings)
                    shops << shopWebObject
                }
            })

            homeWebObject.featuredShops = shops
        }

        // Done

        return new WebView().data([home: homeWebObject] as Map<String, Object>);
    }
}
