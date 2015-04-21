/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.api.v1

import com.google.common.base.Optional
import com.sun.jersey.core.header.FormDataContentDisposition
import com.sun.jersey.multipart.FormDataParam
import groovy.transform.CompileStatic
import org.mayocat.attachment.model.Attachment
import org.mayocat.attachment.store.AttachmentStore
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.cms.home.model.HomePage
import org.mayocat.cms.home.store.HomePageStore
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.PlatformSettings
import org.mayocat.context.WebContext
import org.mayocat.image.model.Image
import org.mayocat.image.model.Thumbnail
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.model.Entity
import org.mayocat.model.EntityList
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.api.delegate.AttachmentApiDelegate
import org.mayocat.rest.api.delegate.EntityApiDelegateHandler
import org.mayocat.shop.catalog.api.v1.object.ProductApiObject
import org.mayocat.shop.catalog.api.v1.object.ShopHomePageApiObject
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.shop.taxes.configuration.TaxesSettings
import org.mayocat.store.EntityListStore
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo

/**
 * /api/home/ API
 *
 * @version $Id$
 */
@Component("/tenant/{tenant}/api/home")
@Path("/tenant/{tenant}/api/home")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
@CompileStatic
class TenantHomePageApi implements Resource, AttachmentApiDelegate
{
    @Inject
    Provider<ProductStore> productStore

    @Inject
    Provider<EntityListStore> entityListStore

    @Inject
    Provider<ThumbnailStore> thumbnailStore

    @Inject
    Provider<AttachmentStore> attachmentStore

    @Inject
    WebContext context

    @Inject
    ConfigurationService configurationService

    @Inject
    PlatformSettings platformSettings

    @Inject
    Provider<HomePageStore> homePageStore

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
        def homePageApiObject = new ShopHomePageApiObject()
        def List<EntityList> lists = entityListStore.get().findListsByHint("home_featured_products");
        if (lists.isEmpty() || lists.first().entities.isEmpty()) {
            homePageApiObject.featuredProducts = []
        } else {
            List<Product> products = productStore.get().findByIds(lists.first().entities)
            def imageIds = products.collect({ Product product -> product.getFeaturedImageId() })
                    .findAll({ UUID id -> id != null})
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

            def taxesSettings = configurationService.getSettings(TaxesSettings.class)

            List<ProductApiObject> featuredProducts = []
            lists.first().entities.each({ UUID id ->
                def product = products.find({ Product product -> product.id == id})
                if (product != null) {
                    def ProductApiObject featuredProduct = new ProductApiObject()
                    featuredProduct.withProduct(taxesSettings, product, Optional.absent())
                    def featuredImage = images.find({ Image image -> image.attachment.id == product.featuredImageId })

                    if (featuredImage) {
                        featuredProduct.withEmbeddedFeaturedImage(featuredImage, context.request.tenantPrefix)
                    }
                    featuredProducts << featuredProduct
                }
            })

            homePageApiObject.featuredProducts = featuredProducts
        }

        // Home

        HomePage homePage = homePageStore.get().getOrCreate(new HomePage())

        if (homePage.getAddons().isLoaded()) {
            homePageApiObject.withAddons(homePage.addons.get())
        }

        homePageApiObject
    }


    @POST
    def updateHomePage(ShopHomePageApiObject homePageApiObject)
    {
        def homeFeaturedList = entityListStore.get().getOrCreate(new EntityList([
                slug: "home-featured-products",
                hint: "home_featured_products",
                type: "product",
                entities: []
        ]))

        Collection<UUID> ids = homePageApiObject.featuredProducts.collect({ProductApiObject product ->
            // Not very efficient since we are doing 1 query per product but this is a write operation not so frequent
            // so that will do it for now.
            def productEntity = productStore.get().findBySlug(product.slug)
            return productEntity?.id
        });

        homeFeaturedList.entities = ids.findAll({UUID id -> id != null}).toList()

        HomePage homePage = homePageApiObject.toHomePage(platformSettings, Optional.fromNullable(this.context.theme?.definition))
        homePageStore.get().update(homePage)

        entityListStore.get().update(homeFeaturedList)
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
