/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.api.v1

import com.google.common.base.Strings
import com.yammer.metrics.annotation.Timed
import groovy.transform.CompileStatic
import org.joda.time.DateTimeZone
import org.mayocat.Slugifier
import org.mayocat.accounts.model.Tenant
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.attachment.MetadataExtractor
import org.mayocat.attachment.model.Attachment
import org.mayocat.attachment.store.AttachmentStore
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.image.model.Image
import org.mayocat.model.Entity
import org.mayocat.rest.Resource
import org.mayocat.rest.api.delegate.AttachmentApiDelegate
import org.mayocat.rest.api.delegate.EntityApiDelegateHandler
import org.mayocat.rest.api.delegate.ImageGalleryApiDelegate
import org.mayocat.rest.api.object.Pagination
import org.mayocat.shop.catalog.api.v1.object.ProductApiObject
import org.mayocat.shop.catalog.api.v1.object.ProductListApiObject
import org.mayocat.shop.catalog.model.Product
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

/**
 * @version $Id$
 */
@Component("/api/products")
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
class ProductApi implements Resource
{

    @Inject
    EntityDataLoader dataLoader

    @Inject
    Provider<MarketplaceProductStore> productStore

    @Inject
    Provider<EntityListStore> entityListStore

    @Inject
    WebContext webContext

    @Inject
    ConfigurationService configurationService

    @Inject
    Provider<AttachmentStore> attachmentStore

    @Inject
    Map<String, MetadataExtractor> extractors

    @Inject
    Slugifier slugifier

    @Inject
    Logger logger

    @Delegate(methodAnnotations = true, parameterAnnotations = true)
    AttachmentApiDelegate attachmentApi

    @Delegate(methodAnnotations = true, parameterAnnotations = true)
    ImageGalleryApiDelegate imageGalleryApi

    @Inject
    GeneralSettings generalSettings

    // Entity handler for delegates

    EntityApiDelegateHandler productHandler = new EntityApiDelegateHandler() {
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

    void initialize()
    {
        attachmentApi = new AttachmentApiDelegate([
                extractors: extractors,
                attachmentStore: attachmentStore,
                slugifier: slugifier,
                handler: productHandler,
                doAfterAttachmentAdded: { String target, Entity entity, String fileName, Attachment created ->
                    switch (target) {
                        case "image-gallery":
                            afterImageAddedToGallery(entity as Product, fileName, created)
                            break;
                    }
                }
        ])
        imageGalleryApi = new ImageGalleryApiDelegate([
                dataLoader: dataLoader,
                attachmentStore: attachmentStore.get(),
                entityListStore: entityListStore.get(),
                handler: productHandler,
                context: webContext
        ])
    }

    @GET
    @Timed
    @Authorized
    def getProducts(@QueryParam("number") @DefaultValue("50") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset,
            @QueryParam("filter") @DefaultValue("") String filter,
            @QueryParam("titleMatches") @DefaultValue("") String titleMatches)
    {
        List<ProductApiObject> productList = [];
        List<EntityAndTenant<Product>> productsAndTenants;
        def totalItems;

        if (!Strings.isNullOrEmpty(titleMatches)) {
            productsAndTenants = productStore.get().findAllWithTitleLike(titleMatches, number, offset)
            totalItems = productStore.get().countAllWithTitleLike(titleMatches);
        } else {
            productsAndTenants = productStore.get().findAllNotVariants(number, offset)
            totalItems = productStore.get().countAllNotVariants()
        }

        List<Product> products = productsAndTenants.collect({ EntityAndTenant<Product> entityAndTenant ->
            entityAndTenant.getEntity()
        })

        List<EntityData<Product>> productsData = dataLoader.load(products, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY)

        productsData.each({ EntityData<Product> productData ->
            Product product = productData.entity
            def productApiObject = new ProductApiObject([
                    _href: "${webContext.request.tenantPrefix}/api/products/${product.slug}"
            ])
            productApiObject.withProduct(taxesSettings, product)

            if (product.addons.isLoaded()) {
                productApiObject.withAddons(product.addons.get())
            }

            def images = productData.getDataList(Image.class)
            def featuredImage = images.find({ Image image -> image.attachment.id == product.featuredImageId })

            Tenant tenant = productsAndTenants.find({ EntityAndTenant<Product> entityAndTenant ->
                return entityAndTenant.getEntity().id == product.id
            }).getTenant();

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
                        urlTemplate: '${tenantPrefix}/api/products?number=${numberOfItems}&offset=${offset}&titleMatches=${titleMatches}&filter=${filter}',
                        urlArguments: [
                                titleMatches: titleMatches,
                                filter: filter,
                                tenantPrefix: webContext.request.tenantPrefix

                        ]
                ]),
                products: productList
        ])

        productListResult
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
