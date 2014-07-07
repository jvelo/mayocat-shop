/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.web

import com.google.common.base.Optional
import com.google.common.math.IntMath
import groovy.transform.CompileStatic
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.configuration.ConfigurationService
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.StandardOptions
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.localization.EntityLocalizationService
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.shop.catalog.CatalogService
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.store.CollectionStore
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.shop.catalog.web.object.CollectionWebObject
import org.mayocat.shop.front.views.ErrorWebView
import org.mayocat.shop.front.views.WebView
import org.mayocat.theme.ThemeDefinition
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.url.EntityURLFactory
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import java.math.RoundingMode

/**
 * Web view for {@link org.mayocat.shop.catalog.model.Collection}
 *
 * @version $Id$
 */
@Component("/collections")
@Path("/collections")
@Produces([MediaType.TEXT_HTML, MediaType.APPLICATION_JSON])
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
@CompileStatic
class CollectionWebView implements Resource
{
    @Inject
    CatalogService catalogService

    @Inject
    Provider<ProductStore> productStore

    @Inject
    EntityDataLoader dataLoader

    @Inject
    WebContext context

    @Inject
    EntityURLFactory urlFactory

    @Inject
    EntityLocalizationService entityLocalizationService

    @Inject
    @Delegate
    ProductListWebViewDelegate listWebViewDelegate

    @GET
    @Path("{slug}")
    def getCollection(@PathParam("slug") String slug, @QueryParam("page") @DefaultValue("1") Integer page)
    {
        final org.mayocat.shop.catalog.model.Collection collection = catalogService.findCollectionBySlug(slug)

        if (collection == null) {
            return new ErrorWebView().status(404)
        }

        final org.mayocat.shop.catalog.model.Collection localized = entityLocalizationService.
                localize(collection) as org.mayocat.shop.catalog.model.Collection
        final int currentPage = page < 1 ? 1 : page

        def context = new HashMap<String, Object>([
                "title"      : localized.title,
                "description": localized.description
        ])

        ThemeDefinition theme = this.context.theme.definition

        EntityData<org.mayocat.shop.catalog.model.Collection> data = dataLoader.
                load(collection, StandardOptions.LOCALIZE)

        Optional<ImageGallery> gallery = data.getData(ImageGallery.class)
        List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

        Integer numberOfProductsPerPage =
                this.context.theme.definition.getPaginationDefinition("collection").itemsPerPage

        Integer offset = (page - 1) * numberOfProductsPerPage
        Integer totalCount = this.productStore.get().countAllForCollection(collection)
        Integer totalPages = IntMath.divide(totalCount, numberOfProductsPerPage, RoundingMode.UP)

        List<Product> products = productStore.get().findForCollection(collection, numberOfProductsPerPage, offset)
        List<EntityData<Product>> productsData = dataLoader.load(products,
                StandardOptions.LOCALIZE,
                AttachmentLoadingOptions.FEATURED_IMAGE_ONLY
        )

        CollectionWebObject collectionWebObject = new CollectionWebObject()
        collectionWebObject.withCollection(entityLocalizationService.localize(collection) as
                org.mayocat.shop.catalog.model.Collection, urlFactory)

        collectionWebObject.withImages(images, collection.featuredImageId, Optional.fromNullable(theme))
        collectionWebObject.withProducts(buildProductListListWebObject(productsData, Optional.of(collection)),
                currentPage, totalPages)

        context.put("collection", collectionWebObject)

        return new WebView().template("collection.html").data(context)
    }
}
