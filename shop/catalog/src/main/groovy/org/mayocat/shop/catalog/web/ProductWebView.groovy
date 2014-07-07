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
import org.mayocat.addons.web.AddonsWebObjectBuilder
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.StandardOptions
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.localization.EntityLocalizationService
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings
import org.mayocat.shop.catalog.model.Collection
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.model.ProductCollection
import org.mayocat.shop.catalog.store.CollectionStore
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.shop.catalog.web.object.ProductWebObject
import org.mayocat.shop.front.context.ContextConstants
import org.mayocat.shop.front.views.ErrorWebView
import org.mayocat.shop.front.views.WebView
import org.mayocat.theme.ThemeDefinition
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.url.EntityURLFactory
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo
import java.math.RoundingMode
import java.text.MessageFormat

/**
 * @version $Id: 9ddf6c6630e27fd5b2f4ae28be90fd784cff200f $
 */
@Component("/products")
@Path("/products")
@Produces([MediaType.TEXT_HTML, MediaType.APPLICATION_JSON])
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
@CompileStatic
class ProductWebView implements Resource
{
    @Inject
    Provider<ProductStore> productStore;

    @Inject
    EntityDataLoader dataLoader

    @Inject
    ConfigurationService configurationService

    @Inject
    Provider<CollectionStore> collectionStoreProvider

    @Inject
    WebContext context

    @Inject
    EntityURLFactory urlFactory

    @Inject
    ThemeFileResolver themeFileResolver

    @Inject
    EntityLocalizationService entityLocalizationService

    @Inject
    AddonsWebObjectBuilder addonsWebObjectBuilder

    @Inject
    @Delegate
    ProductListWebViewDelegate listWebViewDelegate

    @GET
    def getProducts(@QueryParam("page") @DefaultValue("1") Integer page, @Context UriInfo uriInfo)
    {
        final int currentPage = page < 1 ? 1 : page;
        Integer numberOfProductsPerPage =
                context.theme.definition.getPaginationDefinition("products").getItemsPerPage();

        Integer offset = (page - 1) * numberOfProductsPerPage;
        Integer totalCount = this.productStore.get().countAllOnShelf();
        Integer totalPages = IntMath.divide(totalCount, numberOfProductsPerPage, RoundingMode.UP);

        Map<String, Object> context = new HashMap<>();
        context.put(ContextConstants.PAGE_TITLE, "All products");

        List<Product> products = this.productStore.get().findAllOnShelf(numberOfProductsPerPage, offset);
        List<EntityData<Product>> productsData = dataLoader.load(products,
                AttachmentLoadingOptions.FEATURED_IMAGE_ONLY,
                StandardOptions.LOCALIZE
        )

        List<UUID> productIds = products.collect { Product product -> product.id }

        List<Collection> collections = collectionStoreProvider.get().findAllForProductIds(productIds)
        List<ProductCollection> productsCollections = collectionStoreProvider.get().
                findAllProductsCollectionsForIds(productIds)

        products.each({ Product product ->
            def productCollections = productsCollections.findAll { ProductCollection productCollection ->
                productCollection.productId == product.id
            }
            productCollections = productCollections.collect({ ProductCollection pc ->
                collections.find({ Collection c -> pc.collectionId == c.id })
            })
            product.setCollections(productCollections)
        })

        context.put("products", buildProductListWebObject(currentPage, totalPages, productsData, {
            Integer p -> MessageFormat.format("/products/?page={0}", p);
        }));

        return new WebView().template("products.html").data(context);
    }

    @Path("{slug}/{feat1}/{slug1}")
    @GET
    def getProductWithOneFeature(@PathParam("slug") String slug, @PathParam("feat1") String feat1,
            @PathParam("slug1") String slug1)
    {
        def selectedFeatures = [:]
        selectedFeatures.put(feat1, slug1)
        return getProduct(slug, selectedFeatures)
    }

    @Path("{slug}/{feat1}/{slug1}/{feat2}/{slug2}")
    @GET
    def getProductWithTwoFeature(@PathParam("slug") String slug, @PathParam("feat1") String feat1,
            @PathParam("slug1") String slug1, @PathParam("feat2") String feat2,
            @PathParam("slug2") String slug2)
    {
        def selectedFeatures = [:]
        selectedFeatures.put(feat1, slug1)
        selectedFeatures.put(feat2, slug2)
        return getProduct(slug, selectedFeatures)
    }

    @Path("{slug}/{feat1}/{slug1}/{feat2}/{slug2}/{feat3}/{slug3}")
    @GET
    def getProductWithThreeFeature(@PathParam("slug") String slug, @PathParam("feat1") String feat1,
            @PathParam("slug1") String slug1, @PathParam("feat2") String feat2,
            @PathParam("slug2") String slug2, @PathParam("feat3") String feat3,
            @PathParam("slug3") String slug3)
    {
        def selectedFeatures = [:]
        selectedFeatures.put(feat1, slug1)
        selectedFeatures.put(feat2, slug2)
        selectedFeatures.put(feat3, slug3)
        return getProduct(slug, selectedFeatures)
    }

    @Path("{slug}")
    @GET
    def getProduct(final @PathParam("slug") String slug)
    {
        return getProduct(slug, [:])
    }

    def getProduct(final @PathParam("slug") String slug, Map<String, String> selectedFeatures)
    {
        def product = this.productStore.get().findBySlug(slug);
        if (product == null) {
            return new ErrorWebView().status(404);
        }

        List<Collection> collections = collectionStoreProvider.get().findAllForProduct(product);

        product.setCollections(collections);
        if (collections.size() > 0) {
            // Here we take the first collection in the list, but in the future we should have the featured
            // collection as the parent entity of this product
            product.setFeaturedCollection(collections.get(0));
        }

        def context = new HashMap<String, Object>([
                "title"      : product.title,
                "description": product.description
        ])

        EntityData<Product> data = dataLoader.load(product, StandardOptions.LOCALIZE)

        ThemeDefinition theme = this.context.theme?.definition;

        Optional<ImageGallery> gallery = data.getData(ImageGallery.class);
        List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

        ProductWebObject productWebObject = new ProductWebObject()
        productWebObject.withProduct(entityLocalizationService.localize(product) as Product, urlFactory,
                themeFileResolver, configurationService.getSettings(CatalogSettings.class),
                configurationService.getSettings(GeneralSettings.class))
        productWebObject.withAddons(addonsWebObjectBuilder.build(data))
        productWebObject.withImages(images, product.featuredImageId, Optional.fromNullable(theme))

        // Collections / featured collection
        if (product.featuredCollection.isLoaded()) {
            productWebObject.withCollection(entityLocalizationService.localize(product.featuredCollection.get())
                    as org.mayocat.shop.catalog.model.Collection, urlFactory)
        }

        if (product.virtual) {
            def features = productStore.get().findFeatures(product)
            def variants = productStore.get().findVariants(product)

            productWebObject.withFeaturesAndVariants(features, variants, selectedFeatures,
                    configurationService.getSettings(CatalogSettings.class),
                    configurationService.getSettings(GeneralSettings.class), Optional.fromNullable(theme))
        }

        context.put("product", productWebObject);

        return new WebView().template("product.html").model(product.getModel()).data(context);
    }
}
