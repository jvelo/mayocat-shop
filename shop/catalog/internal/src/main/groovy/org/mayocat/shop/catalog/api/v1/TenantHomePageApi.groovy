/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.api.v1

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.attachment.model.Attachment
import org.mayocat.attachment.store.AttachmentStore
import org.mayocat.configuration.ConfigurationService
import org.mayocat.context.WebContext
import org.mayocat.image.model.Image
import org.mayocat.image.model.Thumbnail
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.model.EntityList
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
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
import javax.ws.rs.core.MediaType

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
class TenantHomePageApi implements Resource
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

        entityListStore.get().update(homeFeaturedList)
    }
}
