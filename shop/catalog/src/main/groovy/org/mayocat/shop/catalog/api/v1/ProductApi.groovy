/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.api.v1

import com.google.common.base.Optional
import com.google.common.base.Strings
import com.google.common.collect.Lists
import com.sun.jersey.core.header.FormDataContentDisposition
import com.sun.jersey.multipart.FormDataParam
import com.yammer.metrics.annotation.Timed
import groovy.transform.TypeChecked
import org.mayocat.addons.api.representation.AddonRepresentation
import org.mayocat.attachment.util.AttachmentUtils
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.context.WebContext
import org.mayocat.image.model.Image
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.model.Addon
import org.mayocat.model.Attachment
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.representations.EntityReferenceRepresentation
import org.mayocat.rest.representations.ImageRepresentation
import org.mayocat.rest.resources.AbstractAttachmentResource
import org.mayocat.rest.support.AddonsRepresentationUnmarshaller
import org.mayocat.shop.catalog.api.representations.ProductRepresentation
import org.mayocat.shop.catalog.api.resources.CollectionResource
import org.mayocat.shop.catalog.api.v1.object.FeatureApiObject
import org.mayocat.shop.catalog.api.v1.object.ImageApiObject
import org.mayocat.shop.catalog.api.v1.object.ProductApiObject
import org.mayocat.shop.catalog.api.v1.object.VariantApiObject
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings
import org.mayocat.shop.catalog.meta.ProductEntity
import org.mayocat.shop.catalog.model.Collection
import org.mayocat.shop.catalog.model.Feature
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.store.CollectionStore
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.store.*
import org.mayocat.theme.TypeDefinition
import org.mayocat.theme.FeatureDefinition
import org.slf4j.Logger
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Component("/api/products")
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
@TypeChecked
public class ProductApi extends AbstractAttachmentResource implements Resource
{
    public static final String PATH = Resource.API_ROOT_PATH + ProductEntity.PATH;

    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private Provider<CollectionStore> collectionStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private AddonsRepresentationUnmarshaller addonsRepresentationUnmarshaller;

    @Inject
    private WebContext webContext;

    @Inject
    private CatalogSettings catalogSettings;

    @Inject
    private Logger logger;

    @GET
    @Timed
    @Authorized
    def getProducts(@QueryParam("number") @DefaultValue("50") Integer number,
                    @QueryParam("offset") @DefaultValue("0") Integer offset,
                    @QueryParam("filter") @DefaultValue("") String filter)
    {
        if (filter.equals("uncategorized")) {
            this.wrapInRepresentations(this.productStore.get().findOrphanProducts());
        } else {
            this.wrapInRepresentations(this.productStore.get().findAll(number, offset));
        }
    }

    @Path("{slug}")
    @GET
    @Timed
    def getProduct(@PathParam("slug") String slug)
    {
        def product = this.productStore.get().findBySlug(slug);
        if (product == null) {
            return Response.status(404).build();
        }

        def collections = this.collectionStore.get().findAllForProduct(product);
        def images = this.getImages(slug)

        def productApiObject = new ProductApiObject([
            _href: "/api/products/${slug}/"
        ])

        productApiObject.withProduct(product)
        productApiObject.withCollectionRelationships(collections)
        productApiObject.withEmbeddedImages(images)

        productApiObject;
    }

    @Path("{slug}/images")
    @GET
    def getImages(@PathParam("slug") String slug)
    {
        def images = [];
        def product = this.productStore.get().findBySlug(slug);
        if (product == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        for (Attachment attachment : this.getAttachmentStore().findAllChildrenOf(product,
                ["png", "jpg", "jpeg", "gif"] as List))
        {
            def thumbnails = thumbnailStore.get().findAll(attachment);
            def image = new Image(attachment, thumbnails);

            def imageApiObject = new ImageApiObject()
            imageApiObject.withImage(image)

            if (product.getFeaturedImageId() != null && product.getFeaturedImageId().equals(attachment.getId())) {
                imageApiObject.featured = true
            }

            images << imageApiObject
        }

        images;
    }

    @Path("{slug}/images/{imageSlug}")
    @Authorized
    @DELETE
    @Consumes(MediaType.WILDCARD)
    def detachImage(@PathParam("slug") String slug,
                    @PathParam("imageSlug") String imageSlug)
    {
        def attachment = getAttachmentStore().findBySlug(imageSlug);
        if (attachment == null) {
            return Response.status(404).build();
        }
        try {
            getAttachmentStore().detach(attachment);
            return Response.noContent().build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(404).build();
        }
    }

    @Path("{slug}/images/{imageSlug}")
    @Authorized
    @POST
    @Consumes(MediaType.WILDCARD)
    def updateImage(@PathParam("slug") String slug,
                    @PathParam("imageSlug") String imageSlug, ImageRepresentation image)
    {
        def attachment = getAttachmentStore().findBySlug(imageSlug);
        if (attachment == null) {
            return Response.status(404).build();
        }
        try {
            attachment.setTitle(image.getTitle());
            attachment.setDescription(image.getDescription());
            attachment.setLocalizedVersions(image.getLocalizedVersions());
            getAttachmentStore().update(attachment);
            return Response.noContent().build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(404).build();
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    @Path("{slug}/attachments")
    @Authorized
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    def addAttachment(@PathParam("slug") String slug,
                      @FormDataParam("file") InputStream uploadedInputStream,
                      @FormDataParam("file") FormDataContentDisposition fileDetail,
                      @FormDataParam("title") String title,
                      @FormDataParam("description") String description)
    {
        def product = this.productStore.get().findBySlug(slug);
        if (product == null) {
            return Response.status(404).build();
        }

        def created = this.addAttachment(uploadedInputStream, fileDetail.getFileName(), title, description,
                Optional.of(product.getId()));

        if (product.getFeaturedImageId() == null && AttachmentUtils.isImage(fileDetail.getFileName())
                && created != null) {

            // If this is an image and the product doesn't have a featured image yet, and the attachment was
            // successful, the we set this image as featured image.
            product.setFeaturedImageId(created.getId());

            try {
                this.productStore.get().update(product);
            } catch (EntityDoesNotExistException | InvalidEntityException e) {
                // Fail silently. The attachment has been added successfully, that's what matter
                this.logger.warn("Failed to set first image as featured image for entity {} with id", product.getId());
            }
        }

        return Response.noContent().build();
    }

    @Path("{slug}/move")
    @POST
    @Timed
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
    @Timed
    @Authorized
    def updateProduct(@PathParam("slug") String slug,
                      @Valid ProductRepresentation updatedProductRepresentation)
    {
        try {
            Product product = this.productStore.get().findBySlug(slug);
            if (product == null) {
                return Response.status(404).build();
            } else {
                product.with {
                    setId(product.getId());
                    setTitle(updatedProductRepresentation.getTitle());
                    setDescription(updatedProductRepresentation.getDescription());
                    setModel(updatedProductRepresentation.getModel());
                    setOnShelf(updatedProductRepresentation.getOnShelf());
                    setPrice(updatedProductRepresentation.getPrice());
                    setWeight(updatedProductRepresentation.getWeight());
                    setStock(updatedProductRepresentation.getStock());
                    setLocalizedVersions(updatedProductRepresentation.getLocalizedVersions());
                    setAddons(addonsRepresentationUnmarshaller.unmarshall(updatedProductRepresentation.getAddons()));
                    setType(updatedProductRepresentation.getType());
                }

                // Featured image
                if (updatedProductRepresentation.getFeaturedImage() != null) {
                    ImageRepresentation representation = updatedProductRepresentation.getFeaturedImage();

                    Attachment featuredImage =
                        this.getAttachmentStore().findBySlugAndExtension(representation.getSlug(),
                                representation.getFile().getExtension());
                    if (featuredImage != null) {
                        product.setFeaturedImageId(featuredImage.getId());
                    }
                }

                this.productStore.get().update(product);
            }

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No product with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @Path("{slug}")
    @DELETE
    @Authorized
    @Consumes(MediaType.WILDCARD)
    def deleteProduct(@PathParam("slug") String slug)
    {
        try {
            Product product = this.productStore.get().findBySlug(slug);

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
    @Timed
    @Authorized
    def replaceProduct(@PathParam("slug") String slug, Product newProduct)
    {
        // TODO
        throw new RuntimeException("Not implemented");
    }

    @POST
    @Timed
    @Authorized
    def createProduct(@Valid ProductRepresentation productRepresentation)
    {
        try {
            Product product = new Product();
            product.with {
                setSlug(productRepresentation.getSlug());
                setTitle(productRepresentation.getTitle());

                if (Strings.isNullOrEmpty(getSlug())) {
                    setSlug(this.getSlugifier().slugify(getTitle()));
                }

                setModel(productRepresentation.getModel());
                setDescription(productRepresentation.getDescription());
                setOnShelf(productRepresentation.getOnShelf());
                setPrice(productRepresentation.getPrice());
                setStock(productRepresentation.getStock());
            }

            productStore.get().create(product);
            Product created = productStore.get().findBySlug(product.getSlug());

            // Respond with a created URI relative to this API URL.
            // This will add a location header like http://host/api/<version>/product/my-created-product
            return Response.created(new URI(created.getSlug())).build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("A product with this slug already exists\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Authorized
    @Path("{slug}/variants/")
    def createVariant(@PathParam("slug") String slug, VariantApiObject variantApiObject)
    {
        def product = productStore.get().findBySlug(slug)
        if (!product) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        if (!product.getType().isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity([reason: "Cannot create a variant for a product which type not defined"]).build()
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
            }
            else {
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
            productStore.get().create(productVariant)
        }
        catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity([reason: "Variant already exists"]).build()
        }

    }

    @POST
    @Authorized
    @Path("{slug}/features/")
    def createFeature(FeatureApiObject featuresApiObject)
    {
        System.out.println("Slug: " + featuresApiObject.slug);
        System.out.println("Title: " + featuresApiObject.title);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    def wrapInRepresentations(List<Product> products)
    {
        List<ProductRepresentation> result = new ArrayList<ProductRepresentation>();
        for (Product product : products) {
            result.add(this.wrapInRepresentation(product));
        }
        return result;
    }

    def wrapInRepresentation(Product product)
    {
        ProductRepresentation pr = new ProductRepresentation(product);
        if (product.getAddons().isLoaded()) {
            List<AddonRepresentation> addons = Lists.newArrayList();
            for (Addon a : product.getAddons().get()) {
                addons.add(new AddonRepresentation(a));
            }
            pr.setAddons(addons);
        }
        return pr;
    }
}
