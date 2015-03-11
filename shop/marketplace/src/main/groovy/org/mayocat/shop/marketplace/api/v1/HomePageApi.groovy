/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.api.v1

import com.google.common.base.Optional
import com.sun.jersey.core.header.FormDataContentDisposition
import com.sun.jersey.multipart.FormDataParam
import groovy.transform.CompileStatic
import org.joda.time.DateTimeZone
import org.mayocat.accounts.api.v1.object.TenantApiObject
import org.mayocat.accounts.model.Tenant
import org.mayocat.accounts.store.TenantStore
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.attachment.model.Attachment
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.cms.home.model.HomePage
import org.mayocat.cms.home.store.HomePageStore
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.PlatformSettings
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.StandardOptions
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.image.model.Thumbnail
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.model.Entity
import org.mayocat.model.EntityList
import org.mayocat.rest.Resource
import org.mayocat.rest.api.delegate.AttachmentApiDelegate
import org.mayocat.rest.api.delegate.EntityApiDelegateHandler
import org.mayocat.shop.catalog.api.v1.object.ProductApiObject
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.shop.marketplace.api.v1.object.MarketplaceHomePageApiObject
import org.mayocat.shop.marketplace.store.MarketplaceProductStore
import org.mayocat.shop.taxes.configuration.TaxesSettings
import org.mayocat.store.EntityListStore
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

/**
 * @version $Id$
 */
@Component("/api/home")
@Path("/api/home")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
class HomePageApi implements Resource, AttachmentApiDelegate
{
    @Inject
    EntityDataLoader dataLoader

    @Inject
    Provider<TenantStore> tenantStore

    @Inject
    Provider<ProductStore> productStore

    @Inject
    Provider<MarketplaceProductStore> marketplaceProductStore

    @Inject
    Provider<EntityListStore> entityListStore

    @Inject
    Provider<ThumbnailStore> thumbnailStore

    @Inject
    Provider<HomePageStore> homePageStore

    @Inject
    WebContext context

    @Inject
    ConfigurationService configurationService

    @Inject
    GeneralSettings generalSettings

    @Inject
    PlatformSettings platformSettings

    EntityApiDelegateHandler getHandler()
    {
        return new EntityApiDelegateHandler() {
            Entity getEntity(String slug)
            {
                homePageStore.get().getOrCreate(new HomePage())
            }

            void updateEntity(Entity entity)
            {
                homePageStore.get().update(entity as HomePage)
            }

            String type()
            {
                "home"
            }
        }
    }

    Closure doAfterAttachmentAdded = { String target, Entity entity, String fileName, Attachment created ->
        // Nothing
    }

    @GET
    def getHomePage()
    {
        def homePageApiObject = new MarketplaceHomePageApiObject()

        // 1. Retrieve featured products

        def List<EntityList> lists = entityListStore.get().findListsByHint("home_featured_products");
        if (lists.isEmpty() || lists.first().entities.isEmpty()) {
            homePageApiObject.featuredProducts = []
        } else {
            List<Product> products = productStore.get().findByIds(lists.first().entities)
            def imageIds = products.collect({ Product product -> product.getFeaturedImageId() })
                    .findAll({ UUID id -> id != null })
            List<Image> images;
            if (imageIds.size() > 0) {
                List<Attachment> attachments = this.attachmentStore.get().findByIds(imageIds.toList());
                List<Thumbnail> thumbnails = this.thumbnailStore.get().findAllForIds(imageIds.toList());
                images = attachments.collect({ Attachment attachment ->
                    def thumbs = thumbnails.findAll({ Thumbnail thumbnail -> thumbnail.attachmentId = attachment.id })
                    return new Image(attachment, thumbs.toList())
                });
            } else {
                images = []
            }

            List<UUID> tenantIds = products.collect({ Product p -> p.tenantId });
            List<Tenant> tenants = tenantStore.get().findByIds(tenantIds)

            def taxesSettings = configurationService.getSettings(TaxesSettings.class)

            List<ProductApiObject> featuredProducts = []
            lists.first().entities.each({ UUID id ->
                def product = products.find({ Product product -> product.id == id })
                if (product != null) {
                    def Tenant tenant = tenants.find({ Tenant tenant -> tenant.id == product.tenantId })

                    def ProductApiObject featuredProduct = new ProductApiObject()
                    featuredProduct.withProduct(taxesSettings, product, Optional.absent())
                    featuredProduct.withEmbeddedTenant(tenant, globalTimeZone)
                    def featuredImage = images.find({ Image image -> image.attachment.id == product.featuredImageId })

                    if (featuredImage) {
                        featuredProduct.withEmbeddedFeaturedImage(featuredImage, "/tenant/${tenant.slug}")
                    }
                    featuredProducts << featuredProduct
                }
            })

            homePageApiObject.featuredProducts = featuredProducts
        }

        // 2. Retrieve featured tenants

        def List<EntityList> tenantLists = entityListStore.get().findListsByHint("home_featured_tenants");
        if (tenantLists.isEmpty() || tenantLists.first().entities.isEmpty()) {
            homePageApiObject.featuredTenants = []
        } else {
            def List<Tenant> tenants = tenantStore.get().findByIds(tenantLists.first().entities);

            def List<EntityData> tenantsData = dataLoader.load(tenants,
                    StandardOptions.LOCALIZE, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY);

            List<TenantApiObject> featuredTenants = [];

            tenantLists.first().entities.each({ UUID tenantId ->

                def EntityData<Tenant> data
                tenantsData.each({ EntityData<Tenant> tenantData ->
                    if (tenantData.entity.id == tenantId) {
                        data = tenantData
                    }
                })

                // I don't know why the groovy compiler won't compile the line below instead :/
                // It fails with :
                // [Static type checking] - Cannot assign value of type org.mayocat.accounts.model.Tenant
                // to variable of type org.mayocat.entity.EntityData <Tenant>

                // def EntityData<Tenant> data = tenantsData.find({ EntityData<Tenant> tenantData -> tenantData.entity.id == tenantId})

                if (data) {
                    def Tenant tenant = data.entity
                    def TenantApiObject tenantApiObject = new TenantApiObject()

                    tenantApiObject.withTenant(tenant, globalTimeZone)

                    def gallery = data.getData(ImageGallery.class)
                    List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

                    tenantApiObject.withEmbeddedImages(images, tenant.featuredImageId, "/tenant/${tenant.slug}")

                    featuredTenants << tenantApiObject
                }
            })

            homePageApiObject.featuredTenants = featuredTenants
        }

        // Home

        HomePage homePage = homePageStore.get().getOrCreate(new HomePage())

        if (homePage.getAddons().isLoaded()) {
            homePageApiObject.withAddons(homePage.addons.get())
        }

        homePageApiObject
    }

    @POST
    def updateHomePage(MarketplaceHomePageApiObject homePageApiObject)
    {
        // 1. Update featured products

        def homeFeaturedProductsList = entityListStore.get().getOrCreate(new EntityList([
                slug    : "home-featured-products",
                hint    : "home_featured_products",
                type    : "product",
                entities: []
        ]))

        Collection<UUID> productIds = homePageApiObject.featuredProducts.collect({ ProductApiObject product ->
            def tenantSlug = (product._embedded.get("tenant") as Map<String, Object>).get("slug") as String

            // Not very efficient since we are doing 1 query per product but this is a write operation not so frequent
            // so that will do it for now.
            def productEntity = marketplaceProductStore.get().findBySlugAndTenant(product.slug, tenantSlug)
            return productEntity?.id
        });

        homeFeaturedProductsList.entities = productIds.findAll({ UUID id -> id != null }).toList()
        entityListStore.get().update(homeFeaturedProductsList)

        // 2. Update featured tenants

        def homeFeaturedTenantsList = entityListStore.get().getOrCreate(new EntityList([
                slug    : "home-featured-tenants",
                hint    : "home_featured_tenants",
                type    : "tenant",
                entities: []
        ]))

        Collection<UUID> tenantIds = homePageApiObject.featuredTenants.collect({ TenantApiObject tenantApiObject ->
            Tenant tenant = this.tenantStore.get().findBySlug(tenantApiObject.slug)
            return tenant.id
        });

        homeFeaturedTenantsList.entities = tenantIds.findAll({ UUID id -> id != null }).toList()
        entityListStore.get().update(homeFeaturedTenantsList)

        // Home page

        HomePage homePage = homePageApiObject.toHomePage(platformSettings, Optional.absent())
        homePageStore.get().update(homePage)

        return Response.ok().build()
    }

    DateTimeZone getGlobalTimeZone()
    {
        return DateTimeZone.forTimeZone(generalSettings.getTime().getTimeZone().getDefaultValue())
    }

    // Delegate to attachments and images API delegates, but without their {{slug}} prefixes (meant for product, pages, etc.)
    @Path("attachments")
    @Authorized
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    def addAttachment(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("filename") String sentFilename,
            @FormDataParam("title") String title,
            @FormDataParam("description") String description,
            @FormDataParam("target") String target,
            @Context UriInfo uriInfo)
    {
        addAttachment('home', uploadedInputStream, fileDetail, sentFilename, title, description, target,
                uriInfo)
    }
}
