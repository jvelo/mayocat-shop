/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.api.v1

import com.google.common.base.Optional
import com.google.common.base.Strings
import groovy.transform.CompileStatic
import org.joda.time.DateTimeZone
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.attachment.model.Attachment
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.PlatformSettings
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.model.Entity
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.api.delegate.AttachmentApiDelegate
import org.mayocat.rest.api.delegate.EntityApiDelegateHandler
import org.mayocat.rest.api.delegate.ImageGalleryApiDelegate
import org.mayocat.rest.api.object.LinkApiObject
import org.mayocat.rest.api.object.Pagination
import org.mayocat.shop.catalog.api.v1.object.ProductApiObject
import org.mayocat.shop.catalog.api.v1.object.ProductListApiObject
import org.mayocat.shop.catalog.api.v1.object.VariantApiObject
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings
import org.mayocat.shop.catalog.model.Feature
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.store.CollectionStore
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.shop.taxes.configuration.Mode
import org.mayocat.shop.taxes.configuration.Rate
import org.mayocat.shop.taxes.configuration.TaxesSettings
import org.mayocat.store.*
import org.mayocat.theme.FeatureDefinition
import org.mayocat.theme.ThemeDefinition
import org.mayocat.theme.TypeDefinition
import org.slf4j.Logger
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.math.RoundingMode

@Component("/tenant/{tenant}/api/products")
@Path("/tenant/{tenant}/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
@CompileStatic
class TenantProductApi implements Resource, AttachmentApiDelegate, ImageGalleryApiDelegate
{
    @Inject
    Provider<ProductStore> productStore

    @Inject
    Provider<CollectionStore> collectionStore

    @Inject
    Provider<EntityListStore> entityListStore

    @Inject
    WebContext webContext

    @Inject
    CatalogSettings catalogSettings

    @Inject
    PlatformSettings platformSettings

    @Inject
    GeneralSettings generalSettings

    @Inject
    ConfigurationService configurationService

    @Inject
    Logger logger

    EntityApiDelegateHandler handler = new EntityApiDelegateHandler() {
        Entity getEntity(String slug)
        {
            return productStore.get().findBySlug(slug)
        }

        void updateEntity(Entity entity)
        {
            productStore.get().update(entity as Product)
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
            @QueryParam("filter") @DefaultValue("") String filter,
            @QueryParam("titleMatches") @DefaultValue("") String titleMatches)
    {
        List<ProductApiObject> productList = [];
        def products;
        def totalItems;

        if (filter.equals("uncategorized")) {
            products = this.productStore.get().findOrphanProducts();
            totalItems = products.size()
        } else if (!Strings.isNullOrEmpty(titleMatches)) {
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

            if (featuredImage) {
                productApiObject.withEmbeddedFeaturedImage(featuredImage, webContext.request.tenantPrefix)
            }

            productList << productApiObject
        })

        def productListResult = new ProductListApiObject([
                _pagination: new Pagination([
                        numberOfItems: number,
                        returnedItems: productList.size(),
                        offset       : offset,
                        totalItems   : totalItems,
                        urlTemplate  : '${tenantPrefix}/api/products?number=${numberOfItems}&offset=${offset}&titleMatches=${titleMatches}&filter=${filter}',
                        urlArguments : [
                                titleMatches: titleMatches,
                                filter      : filter,
                                tenantPrefix: webContext.request.tenantPrefix

                        ]
                ]),
                products   : productList
        ])

        productListResult
    }

    @Path("{slug}")
    @GET
    def getProduct(@PathParam("slug") String slug)
    {
        def product = this.productStore.get().findBySlug(slug);
        if (product == null) {
            return Response.status(404).build();
        }

        def productData = dataLoader.load(product)

        def collections = this.collectionStore.get().findAllForProduct(product);

        def gallery = productData.getData(ImageGallery.class)
        List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

        def productApiObject = new ProductApiObject([
                _href : "${webContext.request.tenantPrefix}/api/products/${slug}",
                _links: [
                        self  : new LinkApiObject([href: "${webContext.request.tenantPrefix}/api/products/${slug}"]),
                        images: new LinkApiObject(
                                [href: "${webContext.request.tenantPrefix}/api/products/${slug}/images"])
                ]
        ])

        productApiObject.withProduct(taxesSettings, product, Optional.absent())
        productApiObject.withCollectionRelationships(collections)
        productApiObject.withEmbeddedImages(images, product.featuredImageId, webContext.request.tenantPrefix)
        productApiObject.withEmbeddedTenant(webContext.tenant, globalTimeZone)

        if (product.addons.isLoaded()) {
            productApiObject.withAddons(product.addons.get())
        }

        if (product.type.isPresent()) {
            productApiObject.
                    withEmbeddedVariants(taxesSettings, productStore.get().findVariants(product), product, webContext.request)
            productApiObject._links.variants =
                    new LinkApiObject([href: "${webContext.request.tenantPrefix}/api/products/${slug}/variants"])
        }

        productApiObject;
    }

    @Path("{slug}/move")
    @POST
    @Authorized
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.WILDCARD)
    def move(@PathParam("slug") String slug,
            @FormParam("before") String slugOfProductToMoveBeforeOf,
            @FormParam("after") String slugOfProductToMoveAfterTo)
    {
        try {
            if (!Strings.isNullOrEmpty(slugOfProductToMoveAfterTo)) {
                this.productStore.get().moveProduct(slug, slugOfProductToMoveAfterTo,
                        HasOrderedCollections.RelativePosition.AFTER);
            } else {
                this.productStore.get().moveProduct(slug, slugOfProductToMoveBeforeOf,
                        HasOrderedCollections.RelativePosition.BEFORE);
            }

            return Response.noContent().build();
        } catch (InvalidMoveOperation e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid move operation").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
    }

    @Path("{slug}")
    @POST
    @Authorized
    def updateProduct(@PathParam("slug") String slug, ProductApiObject productApiObject)
    {
        try {
            Product product = this.productStore.get().findBySlug(slug);
            if (product == null) {
                return Response.status(404).build();
            }
            def id = product.id
            def featuredImageId = product.featuredImageId

            Optional<String> originalVatRateId = product.vatRateId;

            product = productApiObject.toProduct(taxesSettings, platformSettings,
                    Optional.<ThemeDefinition> fromNullable(webContext.theme?.definition))
            // ID and slugs are not update-able
            product.id = id
            product.slug = slug

            // Featured image is updated via the /images API only, set it back
            product.featuredImageId = featuredImageId

            // Check if virtual flag must be removed or set
            if (Strings.isNullOrEmpty(productApiObject.type) && product.virtual) {
                // It had a type but no it hasn't
                product.virtual = false;
            } else if (!Strings.isNullOrEmpty(productApiObject.type) && !product.virtual) {
                product.virtual = true;
            }

            this.productStore.get().update(product);

            if (taxesSettings.mode.value.equals(Mode.INCLUSIVE_OF_TAXES) && !product.vatRateId.equals(originalVatRateId)) {
                // In INCL mode, if the VAT rate has changed, we must update all variant EXCL prices according
                // to the new rate.

                // First get the rate value from settings
                BigDecimal newRate = getRateFromId(product.vatRateId)
                BigDecimal oldRate = getRateFromId(originalVatRateId)

                List<Product> variants = productStore.get().findVariants(product)
                variants.each({ Product variant ->
                    if (!variant.vatRateId.isPresent()) {
                        // If the variant itself has a VAT rate defined, ignore. Otherwise convert to new rate.

                        // First convert to inclusive price
                        BigDecimal inclPrice = BigDecimal.ONE.add(oldRate).multiply(variant.price)

                        // Then convert inclusive to exclusive price with new rate
                        BigDecimal conversionRate = BigDecimal.ONE.
                                divide(BigDecimal.ONE.add(newRate), 10, RoundingMode.HALF_UP)
                        variant.price = inclPrice.multiply(conversionRate)
                        productStore.get().update(variant)
                    }
                })
            }

            // Update variants order if needed
            // TODO : do this in a PUT "{slug}/variants" API instead

            if (productApiObject._embedded?.containsKey("variants")) {
                List<Map<String, Object>> variantApiObjects =
                        productApiObject._embedded?.get("variants") as List<Map<String, Object>>;

                List<Product> existingVariants = productStore.get().findVariants(product);

                variantApiObjects.eachWithIndex({ Map<String, Object> entry, Integer i ->
                    def index = existingVariants.findIndexOf({ Product p -> p.slug == entry.get("slug") })
                    if (index != i) {
                        Product variant = existingVariants.find({ Product p -> p.slug == entry.get("slug") })
                        productStore.get().updatePosition(i, variant);
                    }
                })
            }

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    private BigDecimal getRateFromId(Optional<String> id)
    {
        BigDecimal vatRate
        if (id.isPresent()) {
            vatRate = taxesSettings.vat.value.otherRates
                    .find({ Rate rate -> rate.id == id.get() })?.value;
        }
        if (!vatRate) {
            vatRate = taxesSettings.vat.value.defaultRate
        }
        vatRate
    }

    @Path("{slug}")
    @DELETE
    @Authorized
    @Consumes(MediaType.WILDCARD)
    def deleteProduct(@PathParam("slug") String slug)
    {
        try {
            def product = this.productStore.get().findBySlug(slug);

            if (product == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No product with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
            }

            this.productStore.get().delete(product);

            return Response.noContent().build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No product with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @Path("{slug}")
    @PUT
    @Authorized
    def replaceProduct(@PathParam("slug") String slug, Product newProduct)
    {
        // TODO
        throw new RuntimeException("Not implemented");
    }

    @POST
    @Authorized
    def createProduct(ProductApiObject productApiObject)
    {
        try {
            def product = productApiObject.toProduct(taxesSettings, platformSettings,
                    Optional.<ThemeDefinition> fromNullable(webContext.theme?.definition))

            if (Strings.isNullOrEmpty(product.slug) && Strings.isNullOrEmpty(product.title)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("No title nor slug provided\n").type(MediaType.TEXT_PLAIN_TYPE).build();
            }

            // Set slug TODO: verify if provided slug is conform
            product.slug = Strings.isNullOrEmpty(productApiObject.slug) ? slugifier.slugify(product.title) :
                    productApiObject.slug

            def created = productStore.get().create(product);

            // Respond with a created URI relative to this API URL.
            // This will add a location header like http://host/api/<version>/product/my-created-product
            return Response.created(new URI(created.slug)).build();
        }

        catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("A product with this slug already exists\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{slug}/variants")
    def getProductVariants(@PathParam("slug") String slug)
    {
        def product = productStore.get().findBySlug(slug)
        if (!product) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        def variants = productStore.get().findVariants(product)

        List<ProductApiObject> variantApiObjects = []

        variants.each({ Product variant ->
            ProductApiObject object = new ProductApiObject([
                    _href: "${webContext.request.tenantPrefix}/api/products/${product.slug}/variants/${variant.slug}"
            ])
            object.withProduct(taxesSettings, variant, Optional.of(product))
            if (variant.addons.isLoaded()) {
                object.withAddons(variant.addons.get())
            }
            variantApiObjects << object
        })

        variantApiObjects
    }

    @POST
    @Authorized
    @Path("{slug}/variants")
    def createVariant(@PathParam("slug") String slug, VariantApiObject variantApiObject)
    {
        def product = productStore.get().findBySlug(slug)
        if (!product) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        if (!product.getType().isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity([reason: "Cannot create a variant for a product which type is not defined"]).build()
        }

        String type = product.type.get()

        TypeDefinition typeDefinition
        def platformTypes = catalogSettings.productsSettings.types
        def themeTypes = webContext.theme?.definition?.productTypes

        if (platformTypes && platformTypes.containsKey(type)) {
            typeDefinition = platformTypes[type]
        } else if (themeTypes && themeTypes.containsKey(type)) {
            typeDefinition = themeTypes[type]
        }

        if (!typeDefinition) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity([reason: """Type definition for [${type}] not found"""]).build()
        }

        List<Feature> variantFeatures = [];

        for (feature in variantApiObject.features.entrySet()) {

            // 1. Check if the feature is valid, according to the type definition

            if (!typeDefinition.features.containsKey(feature.key)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity([reason: "Feature [${feature.key}] is not allowed for type [${type}]"]).build()
            }

            FeatureDefinition variant = typeDefinition.features.get(feature.key);

            if (variant.keys.size() > 0 && !variant.keys.containsKey(feature.value)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity([reason: "Value [${feature.value}] is not allowed for variant [${feature.key}]"])
                        .build()
            }

            // 2. Check if feature exists, and create it if not

            String title = variant.keys.containsKey(feature.value) ?
                    variant.keys.get(feature.value) : feature.value
            String featureSlug = variant.keys.containsKey(feature.value) ? feature.value : slugifier.slugify(title)

            def feat = productStore.get().findFeature(product, feature.key, featureSlug);
            if (!feat) {
                // It does not exist, we create it
                def featureToCreate = new Feature();

                featureToCreate.with {
                    setParentId(product.id)
                    setSlug(feature.key + "-" + featureSlug)
                    setFeature(feature.key)
                    setFeatureSlug(featureSlug)
                    setTitle(title)
                }

                featureToCreate = productStore.get().createFeature(featureToCreate);
                variantFeatures << featureToCreate
            } else {
                variantFeatures << feat
            }
        }

        // Check if product is virtual, update if it is not

        if (!product.virtual) {
            product.virtual = true;
            productStore.get().update(product);
        }

        // Create variant

        def productVariant = new Product()
        def variantSlug = variantFeatures.collect({ Feature f -> f.featureSlug }).join("-")
        productVariant.with {
            setParentId(product.id)
            setFeatures(variantFeatures.collect({ Feature f -> f.id }))
            setSlug(variantSlug)
            setTitle(variantFeatures.collect({ Feature f -> f.title }).join(" - "))
        }

        try {
            Product created = productStore.get().create(productVariant)

            if (typeDefinition.getVariants().properties.indexOf("stock") >= 0) {
                List<Product> products = this.productStore.get().findVariants(product);
                if (!products.any({ Product v -> v.stock && v.stock > 0 })) {
                    product.stock = 0
                    productStore.get().update(product)
                } else {
                    product.stock = null
                    productStore.get().update(product)
                }
            }

            return Response.created(new URI(created.slug)).build();
        }
        catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity([reason: "Variant already exists"]).build()
        }
    }

    @POST
    @Authorized
    @Path("{slug}/variants/{variantSlug}")
    def updateVariant(@PathParam("slug") String slug, @PathParam("variantSlug") String variantSlug,
            VariantApiObject variantApiObject)
    {
        try {
            Product product = this.productStore.get().findBySlug(slug);
            if (product == null || !product.type.isPresent()) {
                return Response.status(404).build();
            }

            TypeDefinition typeDefinition = getTypeDefinition(product)

            if (!typeDefinition) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity([reason: """Type definition for [${product.type}] not found"""]).build()
            }

            Product variant = productStore.get().findVariant(product, variantSlug);

            if (variant == null) {
                return Response.status(404).build();
            }

            def id = variant.id
            variant = variantApiObject.toProduct(taxesSettings, platformSettings,
                    Optional.<ThemeDefinition> fromNullable(webContext.theme?.definition), Optional.of(product))
            // ID and slugs are not update-able
            variant.id = id
            variant.slug = variantSlug
            variant.parentId = product.id

            this.productStore.get().update(variant)

            if (typeDefinition.getVariants().properties.indexOf("stock") >= 0) {
                List<Product> products = this.productStore.get().findVariants(product);
                if (!products.any({ Product v -> v.stock && v.stock > 0 })) {
                    product.stock = 0
                    productStore.get().update(product)
                } else {
                    product.stock = null
                    productStore.get().update(product)
                }
            }

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Authorized
    @Path("{slug}/variants/{variantSlug}")
    @Consumes(MediaType.WILDCARD)
    def deleteVariant(@PathParam("slug") String slug, @PathParam("variantSlug") String variantSlug)
    {
        Product product = this.productStore.get().findBySlug(slug);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No product with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }

        Product variant = productStore.get().findVariant(product, variantSlug);

        if (variant == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No variant with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }

        this.productStore.get().delete(variant);

        return Response.noContent().build();
    }

    private def TypeDefinition getTypeDefinition(Product product)
    {
        TypeDefinition typeDefinition
        def platformTypes = catalogSettings.productsSettings.types
        def themeTypes = webContext.theme?.definition?.productTypes

        if (platformTypes && platformTypes.containsKey(product.type.get())) {
            typeDefinition = platformTypes[product.type.get()]
        } else if (themeTypes && themeTypes.containsKey(product.type.get())) {
            typeDefinition = themeTypes[product.type.get()]
        }
        typeDefinition
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
