/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.api.v1

import com.google.common.base.Optional
import com.google.common.base.Strings
import groovy.transform.CompileStatic
import org.joda.time.DateTimeZone
import org.mayocat.accounts.model.Tenant
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.attachment.model.Attachment
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.image.model.Image
import org.mayocat.model.Entity
import org.mayocat.model.EntityAndParent
import org.mayocat.rest.Reference
import org.mayocat.rest.Resource
import org.mayocat.rest.api.delegate.AttachmentApiDelegate
import org.mayocat.rest.api.delegate.EntityApiDelegateHandler
import org.mayocat.rest.api.delegate.ImageGalleryApiDelegate
import org.mayocat.rest.api.object.Pagination
import org.mayocat.shop.catalog.api.v1.object.CollectionApiObject
import org.mayocat.shop.catalog.api.v1.object.ProductApiObject
import org.mayocat.shop.catalog.api.v1.object.ProductListApiObject
import org.mayocat.shop.catalog.model.Collection
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.store.CollectionStore
import org.mayocat.shop.marketplace.api.v1.object.ProductCollectionsApiObject
import org.mayocat.shop.marketplace.model.EntityAndTenant
import org.mayocat.shop.marketplace.store.MarketplaceProductStore
import org.mayocat.shop.taxes.configuration.TaxesSettings
import org.mayocat.store.EntityListStore
import org.slf4j.Logger
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/api/products")
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
class ProductApi implements Resource, AttachmentApiDelegate, ImageGalleryApiDelegate
{
    @Inject
    Provider<MarketplaceProductStore> productStore

    @Inject
    Provider<CollectionStore> collectionStore

    @Inject
    Provider<EntityListStore> entityListStore

    @Inject
    WebContext webContext

    @Inject
    ConfigurationService configurationService

    @Inject
    Logger logger

    @Inject
    GeneralSettings generalSettings

    // Entity handler for delegates

    EntityApiDelegateHandler handler = new EntityApiDelegateHandler() {
        Entity getEntity(String slug)
        {
            return null;
            //return productStore.get().findBySlug(slug)
        }

        void updateEntity(Entity entity)
        {
            return;
            //productStore.get().update(entity)
        }

        String type()
        {
            "product"
        }
    }

    Closure doAfterAttachmentAdded = { String target, Entity entity, String fileName, Attachment created ->
        switch (target) {
            case "image-gallery":
                afterImageAddedToGallery(entity as Product, fileName, created)
                break;
        }
    }

    @GET
    @Authorized
    def getProducts(@QueryParam("number") @DefaultValue("50") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset,
            @QueryParam("titleMatches") @DefaultValue("") String titleMatches)
    {
        List<ProductApiObject> productList = [];
        List<Product> products;
        def totalItems;

        if (!Strings.isNullOrEmpty(titleMatches)) {
            products = productStore.get().findAllWithTitleLike(titleMatches, number, offset)
            totalItems = productStore.get().countAllWithTitleLike(titleMatches);
        } else {
            products = productStore.get().findAllNotVariants(number, offset)
            totalItems = productStore.get().countAllNotVariants()
        }

        List<EntityData<Product>> productsData = dataLoader.load(products, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY)

        productsData.each({ EntityData<Product> productData ->
            Product product = productData.entity
            def productApiObject = new ProductApiObject([
                    _href: "${webContext.request.tenantPrefix}/api/products/${product.slug}"
            ])
            productApiObject.withProduct(taxesSettings, product, Optional.absent())

            if (product.addons.isLoaded()) {
                productApiObject.withAddons(product.addons.get())
            }

            def images = productData.getDataList(Image.class)
            def featuredImage = images.find({ Image image -> image.attachment.id == product.featuredImageId })
            def tenant = productData.getData(Tenant.class).orNull()

            if (featuredImage) {
                productApiObject.withEmbeddedFeaturedImage(featuredImage, "/tenant/${tenant.slug}")
            }

            productApiObject.withEmbeddedTenant(tenant, getGlobalTimeZone())

            productList << productApiObject
        })

        def productListResult = new ProductListApiObject([
                _pagination: new Pagination([
                        numberOfItems: number,
                        returnedItems: productList.size(),
                        offset: offset,
                        totalItems: totalItems,
                        urlTemplate: '${tenantPrefix}/api/products?number=${numberOfItems}&offset=${offset}&titleMatches=${titleMatches}&',
                        urlArguments: [
                                titleMatches: titleMatches,
                                tenantPrefix: webContext.request.tenantPrefix

                        ]
                ]),
                products: productList
        ])

        productListResult
    }

    @GET
    @Path("{product}/collections")
    def getProductCollections(@PathParam("product") Reference reference)
    {
        Product product = this.productStore.get().
                findBySlugAndTenant(reference.entitySlug, reference.tenantSlug)

        if (!product) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        List<EntityAndParent<Collection>> collections = this.collectionStore.get().findAllForEntity(product)

        List<CollectionApiObject> collectionApiObjects = []

        collections.each({ EntityAndParent<Collection> collection ->
            String href = collection.entity.slug
            EntityAndParent<Collection> parent = collection.parent
            while (parent != null) {
                href = parent.entity.slug + "/collections/" + href
                parent = parent.parent
            }
            href = "/api/collections/" + href;

            CollectionApiObject object = new CollectionApiObject([
                    _href: href
            ])
            object.withCollection(collection.entity)
            if (collection.entity.addons.isLoaded()) {
                object.withAddons(collection.entity.addons.get())
            }

            collectionApiObjects << object
        })

        new ProductCollectionsApiObject([
                collections: collectionApiObjects
        ])
    }

    private def TaxesSettings getTaxesSettings()
    {
        return configurationService.getSettings(TaxesSettings.class)
    }

    private def DateTimeZone getGlobalTimeZone()
    {
        return DateTimeZone.forTimeZone(generalSettings.time.timeZone.defaultValue)
    }

}
