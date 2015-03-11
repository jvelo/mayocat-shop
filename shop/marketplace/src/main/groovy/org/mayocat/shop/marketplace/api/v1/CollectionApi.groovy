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
import com.sun.jersey.core.header.FormDataContentDisposition
import com.sun.jersey.multipart.FormDataParam
import groovy.transform.CompileStatic
import org.joda.time.DateTimeZone
import org.mayocat.Slugifier
import org.mayocat.accounts.model.Tenant
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.attachment.model.Attachment
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.PlatformSettings
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.model.Entity
import org.mayocat.model.EntityAndParent
import org.mayocat.model.PositionedEntity
import org.mayocat.rest.Reference
import org.mayocat.rest.Resource
import org.mayocat.rest.api.delegate.AttachmentApiDelegate
import org.mayocat.rest.api.delegate.EntityApiDelegateHandler
import org.mayocat.rest.api.delegate.ImageGalleryApiDelegate
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.rest.api.object.ImageGalleryApiObject
import org.mayocat.rest.api.object.LinkApiObject
import org.mayocat.rest.api.object.Pagination
import org.mayocat.shop.catalog.CatalogService
import org.mayocat.shop.catalog.api.v1.object.CollectionApiObject
import org.mayocat.shop.catalog.api.v1.object.CollectionItemApiObject
import org.mayocat.shop.catalog.api.v1.object.CollectionTreeApiObject
import org.mayocat.shop.catalog.api.v1.object.ProductApiObject
import org.mayocat.shop.catalog.api.v1.object.ProductListApiObject
import org.mayocat.shop.catalog.model.Collection
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.store.CollectionStore
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.shop.marketplace.store.MarketplaceProductStore
import org.mayocat.shop.taxes.configuration.TaxesSettings
import org.mayocat.store.EntityAlreadyExistsException
import org.mayocat.store.InvalidEntityException
import org.mayocat.theme.ThemeDefinition
import org.slf4j.Logger
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
@Component("/api/collections")
@Path("/api/collections")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
@Authorized
class CollectionApi implements Resource, AttachmentApiDelegate, ImageGalleryApiDelegate
{
    @Inject
    Provider<CollectionStore> collectionStore

    @Inject
    Provider<MarketplaceProductStore> productStore

    @Inject
    Provider<ProductStore> tenantProductStore

    @Inject
    CatalogService catalogService

    @Inject
    Slugifier slugifier

    @Inject
    EntityDataLoader dataLoader

    @Inject
    WebContext context

    @Inject
    PlatformSettings platformSettings

    @Inject
    ConfigurationService configurationService

    @Inject
    Logger logger

    @Inject
    GeneralSettings generalSettings

    EntityApiDelegateHandler handler = new EntityApiDelegateHandler() {
        Entity getEntity(String slug)
        {
            return collectionStore.get().findBySlug(slug)
        }

        void updateEntity(Entity entity)
        {
            collectionStore.get().update(entity as Collection)
        }

        String type()
        {
            "collection"
        }
    }

    Closure doAfterAttachmentAdded = { String target, Entity entity, String fileName, Attachment created ->
        switch (target) {
            case "image-gallery":
                afterImageAddedToGallery(entity as Collection, fileName, created)
                break;
        }
    }

    @GET
    public CollectionTreeApiObject getCollectionTree()
    {
        List<org.mayocat.shop.catalog.model.Collection> collections = collectionStore.get().
                findAllOrderedByParentAndPosition()

        Map<UUID, CollectionItemApiObject> objects = [:]

        List<CollectionItemApiObject> roots = []

        collections.each({ org.mayocat.shop.catalog.model.Collection collection ->
            objects.put(collection.id, new CollectionItemApiObject().withCollection(collection))
        })

        collections.each({ org.mayocat.shop.catalog.model.Collection collection ->
            if (!collection.parentId) {
                roots << objects.get(collection.id)
            } else {
                if (!objects.containsKey(collection.parentId)) {
                    logger.warn("Collection with id {} references a parent that does not exists", collection.id)
                } else {
                    objects.get(collection.parentId).children << objects.get(collection.id)
                }
            }
        })

        // Inject parent slugs & hrefs

        Closure addParentsToItem;
        addParentsToItem = { CollectionItemApiObject item, List<CollectionItemApiObject> parents ->
            item.parentSlugs = parents.collect({ CollectionItemApiObject i -> i.slug })
            item.children.each({ CollectionItemApiObject children ->
                List<CollectionItemApiObject> childrenParents = []
                childrenParents.addAll(parents)
                childrenParents.add(0, item)
                addParentsToItem(children, childrenParents)
            })
        }

        Closure addLinks;
        addLinks = { CollectionItemApiObject item ->
            def parentsChain = item.parentSlugs.reverse().join('/collections/') +
                    (item.parentSlugs.size() > 0 ? '/collections/' : '')
            item._href = "/api/collections/${parentsChain}${item.slug}"
            item._links = [
                    self    : new LinkApiObject([href: item._href]),
                    products: new LinkApiObject([href: item._href + '/products'])
            ]

            item.children.each({ CollectionItemApiObject children ->
                addLinks(children)
            })
        }

        roots.each({ CollectionItemApiObject item ->
            addParentsToItem(item, [])
            addLinks(item)
        })

        new CollectionTreeApiObject([
                collections: roots
        ])
    }

    @PUT
    public void updateCollectionTree(CollectionTreeApiObject collectionTreeApiObject)
    {
        List<org.mayocat.shop.catalog.model.Collection> collections = collectionStore.get().
                findAllOrderedByParentAndPosition()

        List<PositionedEntity<org.mayocat.shop.catalog.model.Collection>> positionedCollections = []

        Closure processItem;
        processItem = { CollectionItemApiObject item, UUID parent, Integer position ->

            org.mayocat.shop.catalog.model.Collection collection = collections.find({
                org.mayocat.shop.catalog.model.Collection c -> c.id.toString() == item._id
            })
            if (!collection) {
                logger.warn("Trying to update a collection that does not exist");
            } else {
                collection.parentId = parent
                positionedCollections <<
                        new PositionedEntity<org.mayocat.shop.catalog.model.Collection>(collection, position)

                item.children.eachWithIndex({ CollectionItemApiObject entry, Integer i ->
                    processItem(entry, collection.id, i)
                })
            }
        }

        collectionTreeApiObject.collections.eachWithIndex({ CollectionItemApiObject item, Integer i ->
            processItem(item, null, i)
        })

        this.collectionStore.get().updateCollectionTree(positionedCollections);
    }

    @POST
    @Authorized
    public Response createCollection(CollectionApiObject collectionApiObject)
    {
        try {

            def collection = collectionApiObject.toCollection(platformSettings,
                    Optional.<ThemeDefinition> fromNullable(context.theme?.definition));

            // Set slug TODO: verify if provided slug is conform
            collection.slug = Strings.isNullOrEmpty(collectionApiObject.slug) ? slugifier.slugify(collection.title) :
                    collectionApiObject.slug

            org.mayocat.shop.catalog.model.Collection created = this.collectionStore.get().create(collection);

            // Respond with a created URI relative to this API URL.
            // This will add a location header like http://host/api/<version>/collections/items/my-created-collection
            return Response.created(new URI(created.slug)).build();
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("A Collection with this slug already exists\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e);
        }
    }

    @Path("{slug}")
    @POST
    def updateCollection(@PathParam("slug") String slug, CollectionApiObject collectionApiObject)
    {
        updateCollectionInternal(collectionApiObject, slug)
    }

    @POST
    @Path("{parent1}/collections/{slug}")
    def updateCollectionWithOneParent(
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            CollectionApiObject collectionApiObject)
    {
        updateCollectionInternal(collectionApiObject, parent1, slug)
    }

    @POST
    @Path("{parent2}/collections/{parent1}/collections/{slug}")
    def updateCollectionWithTwoParents(
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            CollectionApiObject collectionApiObject)
    {
        updateCollectionInternal(collectionApiObject, parent2, parent1, slug)
    }

    @POST
    @Path("{parent3}/collections/{parent2}/collections/{parent1}/collections/{slug}")
    def updateCollectionWithThreeParents(
            @PathParam("parent3") String parent3,
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            CollectionApiObject collectionApiObject)
    {
        updateCollectionInternal(collectionApiObject, parent3, parent2, parent1, slug)
    }

    @GET
    @Path("{slug}")
    def getCollection(@PathParam("slug") String slug)
    {
        getCollectionInternal(slug)
    }

    @GET
    @Path("{parent1}/collections/{slug}")
    def getCollectionWithOneParent(
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug)
    {
        getCollectionInternal(parent1, slug)
    }

    @GET
    @Path("{parent2}/collections/{parent1}/collections/{slug}")
    def getCollectionWithTwoParents(
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug)
    {
        getCollectionInternal(parent2, parent1, slug)
    }

    @GET
    @Path("{parent3}/collections/{parent2}/collections/{parent1}/collections/{slug}")
    def getCollectionWithThreeParents(
            @PathParam("parent3") String parent3,
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug)
    {
        getCollectionInternal(parent3, parent2, parent1, slug)
    }

    @DELETE
    @Path("{slug}")
    def deleteCollection(@PathParam("slug") String slug)
    {
        deleteCollectionInternal(slug)
    }

    @DELETE
    @Path("{parent1}/collections/{slug}")
    def deleteCollectionWithOneParent(
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug)
    {
        deleteCollectionInternal(parent1, slug)
    }

    @DELETE
    @Path("{parent2}/collections/{parent1}/collections/{slug}")
    def deleteCollectionWithTwoParents(
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug)
    {
        deleteCollectionInternal(parent2, parent1, slug)
    }

    @DELETE
    @Path("{parent3}/collections/{parent2}/collections/{parent1}/collections/{slug}")
    def deleteCollectionWithThreeParents(
            @PathParam("parent3") String parent3,
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug)
    {
        deleteCollectionInternal(parent3, parent2, parent1, slug)
    }

    @POST
    @Path("{slug}/products")
    def addProductToCollection(@PathParam("slug") String slug, ProductApiObject productApiObject)
    {
        addProductToCollectionInternal(productApiObject, slug)
    }

    @POST
    @Path("{parent1}/collections/{slug}/products")
    def addProductToCollectionWithOneParent(
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            ProductApiObject productApiObject)
    {
        addProductToCollectionInternal(productApiObject, parent1, slug)
    }

    @POST
    @Path("{parent2}/collections/{parent1}/collections/{slug}/products")
    def addProductToCollectionWithTwoParents(
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            ProductApiObject productApiObject)
    {
        addProductToCollectionInternal(productApiObject, parent2, parent1, slug)
    }

    @POST
    @Path("{parent3}/collections/{parent2}/collections/{parent1}/collections/{slug}/products")
    def addProductToCollectionWithThreeParents(
            @PathParam("parent3") String parent3,
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            ProductApiObject productApiObject)
    {
        addProductToCollectionInternal(productApiObject, parent3, parent2, parent1, slug)
    }

    @DELETE
    @Path("{collection}/products/{product}")
    def removeProductFromCollection(
            @PathParam("collection") String slug,
            @PathParam("product") Reference product)
    {
        removeProductFromCollectionInternal(product, slug)
    }

    @DELETE
    @Path("{parent1}/collections/{slug}/products/{product}")
    def removeProductFromCollectionWithOneParent(
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            @PathParam("product") Reference product)
    {
        removeProductFromCollectionInternal(product, parent1, slug)
    }

    @DELETE
    @Path("{parent2}/collections/{parent1}/collections/{slug}/products/{product}")
    def removeProductFromCollectionWithTwoParents(
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            @PathParam("product") Reference product)
    {
        removeProductFromCollectionInternal(product, parent2, parent1, slug)
    }

    @DELETE
    @Path("{parent3}/collections/{parent2}/collections/{parent1}/collections/{slug}/products/{product}")
    def removeProductFromCollectionWithThreeParents(
            @PathParam("parent3") String parent3,
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            @PathParam("product") Reference product)
    {
        removeProductFromCollectionInternal(product, parent3, parent2, parent1, slug)
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Attachments
    @POST
    @Path("{parent1}/collections/{slug}/attachments")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    def addAttachmentForCollectionWithOneParent(
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("filename") String sentFilename,
            @FormDataParam("title") String title,
            @FormDataParam("description") String description,
            @FormDataParam("target") String target,
            @Context UriInfo info)
    {
        Collection collection = getCollectionFromSlugChain(parent1, slug);
        return addAttachment(collection, fileDetail, sentFilename, uploadedInputStream, title, description, target, info)
    }

    @POST
    @Path("{parent2}/collections/{parent1}/collections/{slug}/attachments")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    def addAttachmentForCollectionWithTwoParents(
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("filename") String sentFilename,
            @FormDataParam("title") String title,
            @FormDataParam("description") String description,
            @FormDataParam("target") String target,
            @Context UriInfo info)
    {
        Collection collection = getCollectionFromSlugChain(parent2, parent1, slug);
        return addAttachment(collection, fileDetail, sentFilename, uploadedInputStream, title, description, target, info)
    }

    @POST
    @Path("{parent3}/collections/{parent2}/collections/{parent1}/collections/{slug}/attachments")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    def addAttachmentForCollectionWithThreeParents(
            @PathParam("parent3") String parent3,
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("filename") String sentFilename,
            @FormDataParam("title") String title,
            @FormDataParam("description") String description,
            @FormDataParam("target") String target,
            @Context UriInfo info)
    {
        Collection collection = getCollectionFromSlugChain(parent3, parent2, parent1, slug);
        return addAttachment(collection, fileDetail, sentFilename, uploadedInputStream, title, description, target, info)
    }

    @GET
    @Path("{parent1}/collections/{slug}/images")
    def getImagesForCollectionWithOneParent(
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug)
    {
        Collection collection = getCollectionFromSlugChain(parent1, slug);
        getImages(collection)
    }

    @GET
    @Path("{parent2}/collections/{parent1}/collections/{slug}/images")
    def getImagesForCollectionWithTwoParents(
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug)
    {
        Collection collection = getCollectionFromSlugChain(parent2, parent1, slug);
        getImages(collection)
    }

    @GET
    @Path("{parent3}/collections/{parent2}/collections/{parent1}/collections/{slug}/images")
    def getImagesForCollectionWithThreeParents(
            @PathParam("parent3") String parent3,
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug)
    {
        Collection collection = getCollectionFromSlugChain(parent3, parent2, parent1, slug);
        getImages(collection)
    }

    @DELETE
    @Path("{parent1}/collections/{slug}/images/{imageSlug}")
    @Consumes(MediaType.WILDCARD)
    def detachImageForCollectionWithOneParent(
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            @PathParam("imageSlug") String imageSlug)
    {
        Collection collection = getCollectionFromSlugChain(parent1, slug);
        detachImages(imageSlug, collection)
    }

    @DELETE
    @Path("{parent2}/collections/{parent1}/collections/{slug}/images/{imageSlug}")
    @Consumes(MediaType.WILDCARD)
    def detachImageForCollectionWithTwoParents(
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            @PathParam("imageSlug") String imageSlug)
    {
        Collection collection = getCollectionFromSlugChain(parent2, parent1, slug);
        detachImages(imageSlug, collection)
    }

    @DELETE
    @Path("{parent3}/collections/{parent2}/collections/{parent1}/collections/{slug}/images/{imageSlug}")
    @Consumes(MediaType.WILDCARD)
    def detachImageForCollectionWithThreeParents(
            @PathParam("parent3") String parent3,
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            @PathParam("imageSlug") String imageSlug)
    {
        Collection collection = getCollectionFromSlugChain(parent3, parent2, parent1, slug);
        detachImages(imageSlug, collection)
    }

    @POST
    @Path("{parent1}/collections/{slug}/images")
    def void updateImageGalleryForCollectionWithOneParent(
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            ImageGalleryApiObject gallery)
    {
        Collection collection = getCollectionFromSlugChain(parent1, slug);
        updateImageGallery(collection, gallery)
    }

    @POST
    @Path("{parent2}/collections/{parent1}/collections/{slug}/images/")
    def void updateImageGalleryForCollectionWithTwoParents(
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            ImageGalleryApiObject gallery)
    {
        Collection collection = getCollectionFromSlugChain(parent2, parent1, slug);
        updateImageGallery(collection, gallery)
    }

    @POST
    @Path("{parent3}/collections/{parent2}/collections/{parent1}/collections/{slug}/images/")
    def void updateImageGalleryForCollectionWithThreeParents(
            @PathParam("parent3") String parent3,
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            ImageGalleryApiObject gallery)
    {
        Collection collection = getCollectionFromSlugChain(parent3, parent2, parent1, slug);
        updateImageGallery(collection, gallery)
    }

    @POST
    @Path("{parent1}/collections/{slug}/images/{imageSlug}")
    @Consumes(MediaType.WILDCARD)
    def Response updateImageForCollectionWithOneParent(
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            @PathParam("imageSlug") String imageSlug,
            ImageApiObject image)
    {
        updateImage(imageSlug, image)
    }

    @POST
    @Path("{parent2}/collections/{parent1}/collections/{slug}/images/{imageSlug}")
    @Consumes(MediaType.WILDCARD)
    def Response updateImageForCollectionWithTwoParents(
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            @PathParam("imageSlug") String imageSlug,
            ImageApiObject image)
    {
        updateImage(imageSlug, image)
    }

    @POST
    @Path("{parent3}/collections/{parent2}/collections/{parent1}/collections/{slug}/images/{imageSlug}")
    @Consumes(MediaType.WILDCARD)
    def Response updateImageForCollectionWithTwoParents(
            @PathParam("parent3") String parent3,
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug,
            @PathParam("imageSlug") String imageSlug,
            ImageApiObject image)
    {
        updateImage(imageSlug, image)
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Products

    @GET
    @Path("{slug}/products")
    def getProductsForCollection(@PathParam("slug") String slug)
    {
        getProducts(slug);
    }

    @GET
    @Path("{parent1}/collections/{slug}/products")
    def getProductsForCollectionWithOneParent(
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug)
    {
        getProducts(parent1, slug);
    }

    @GET
    @Path("{parent2}/collections/{parent1}/collections/{slug}/products")
    def getProductsForCollectionWithTwoParents(
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug)
    {
        getProducts(parent2, parent1, slug);
    }

    @GET
    @Path("{parent3}/collections/{parent2}/collections/{parent1}/collections/{slug}/products")
    def getProductsForCollectionWithThreeParents(
            @PathParam("parent3") String parent3,
            @PathParam("parent2") String parent2,
            @PathParam("parent1") String parent1,
            @PathParam("slug") String slug)
    {
        getProducts(parent3, parent2, parent1, slug);
    }

    // -----------------------------------------------------------------------------------------------------------------

    def removeProductFromCollectionInternal(Reference reference, String... slugsArray)
    {
        Collection collection = getCollectionFromSlugChain(slugsArray);
        String productSlug = reference.entitySlug
        String tenantSlug = reference.tenantSlug

        Product product = this.productStore.get().findBySlugAndTenant(productSlug, tenantSlug)

        if (!collection || !product) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        this.collectionStore.get().removeEntityFromCollection(collection, product);
    }

    def addProductToCollectionInternal(ProductApiObject productApiObject, String... slugsArray)
    {
        Collection collection = getCollectionFromSlugChain(slugsArray);
        String productSlug = productApiObject.slug
        String tenantSlug = (productApiObject._embedded?.tenant as Map)?.slug

        Product product = this.productStore.get().findBySlugAndTenant(productSlug, tenantSlug)

        if (!collection || !product) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        this.collectionStore.get().addEntityToCollection(collection, product)
    }

    def updateCollectionInternal(CollectionApiObject collectionApiObject, String... slugsArray)
    {
        try {
            def org.mayocat.shop.catalog.model.Collection collection = getCollectionFromSlugChain(slugsArray);
            if (collection == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No collection with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).
                        build();
            } else {
                def id = collection.id
                def featuredImageId = collection.featuredImageId
                def slug = collection.slug

                collection = collectionApiObject.toCollection(platformSettings,
                        Optional.<ThemeDefinition> fromNullable(context.theme?.definition))

                // ID and slugs are not update-able
                collection.id = id
                collection.slug = slug

                // Featured image is updated via the /images API only, set it back
                collection.featuredImageId = featuredImageId

                this.collectionStore.get().update(collection);

                return Response.ok().build();
            }
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    def deleteCollectionInternal(String... slugsArray)
    {
        def org.mayocat.shop.catalog.model.Collection collection = getCollectionFromSlugChain(slugsArray);

        if (!collection) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        this.collectionStore.get().delete(collection)

        return Response.ok().build()
    }

    def getProducts(String... slugsArray)
    {
        def org.mayocat.shop.catalog.model.Collection collection = getCollectionFromSlugChain(slugsArray);
        if (!collection) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        final EntityAndParent<Collection> collectionChain = collectionStore.get().findBySlugs(slugsArray)
        List<Product> products = tenantProductStore.get().findAllForCollection(collectionChain)
        List<ProductApiObject> productList = [];
        List<EntityData<Product>> productsData = dataLoader.load(products, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY)

        productsData.each({ EntityData<Product> productData ->
            Product product = productData.entity
            def productApiObject = new ProductApiObject([
                    _href: "${context.request.tenantPrefix}/api/products/${product.slug}"
            ])
            productApiObject.withProduct(taxesSettings, product, Optional.absent())

            if (product.addons.isLoaded()) {
                productApiObject.withAddons(product.addons.get())
            }

            def productImages = productData.getDataList(Image.class)
            def featuredImage = productImages.find({ Image image -> image.attachment.id == product.featuredImageId })
            def tenant = productData.getData(Tenant.class).orNull()

            if (featuredImage) {
                productApiObject.withEmbeddedFeaturedImage(featuredImage, "/tenant/${tenant.slug}")
            }

            productApiObject.withEmbeddedTenant(tenant, getGlobalTimeZone())

            productList << productApiObject
        })

        def productListResult = new ProductListApiObject([
                _pagination: new Pagination([
                        numberOfItems: productList.size(),
                        returnedItems: productList.size(),
                        offset       : 0,
                        totalItems   : productList.size(),
                        urlTemplate: "/api/collections/${collection.slug}/images"
                ]),
                products   : productList
        ])

        productListResult
    }

    def getCollectionInternal(String... slugsArray)
    {
        def org.mayocat.shop.catalog.model.Collection collection = getCollectionFromSlugChain(slugsArray);

        if (!collection) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }
        def slug = collection.slug

        EntityData<org.mayocat.shop.catalog.model.Collection> collectionData = dataLoader.load(collection)

        def gallery = collectionData.getData(ImageGallery)
        List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

        if (collection == null) {
            return Response.status(404).build();
        }

        // TODO fix links : parent chain is missing
        CollectionApiObject collectionApiObject = new CollectionApiObject([
                _href : "/api/collections/${slug}/",
                _links: [
                        self  : new LinkApiObject([href: "/api/collections/${slug}/"]),
                        images: new LinkApiObject(
                                [href: "/api/collections/${slug}/images"]),
                        products: new LinkApiObject(
                                [href: "/api/collections/${slug}/products"])
                ]
        ])

        collectionApiObject.withCollection(collection)
        if (collection.addons.isLoaded()) {
            collectionApiObject.withAddons(collection.addons.get())
        }
        collectionApiObject.withEmbeddedImages(images, collection.featuredImageId, context.request.tenantPrefix)

        collectionApiObject
    }

    private Collection getCollectionFromSlugChain(String... slugsArray)
    {
        // Reverse array
        def slugs = slugsArray.reverse() as List<String>

        List<org.mayocat.shop.catalog.model.Collection> parents = []
        def index = 0

        while (slugs.size() > 1) {
            // Walk down the parents slug chain

            def parentSlug = slugs.pop()
            def parentCollection = this.collectionStore.get().
                    findBySlug(parentSlug, index > 0 ? parents[index - 1].id : null);

            if (!parentCollection) {
                // If any of the collection of the chain is not found, the collection is not found
                return null
            }

            parents.push(parentCollection)
            index++
        }

        def slug = slugs[0]

        org.mayocat.shop.catalog.model.Collection collection = this.collectionStore.get().
                findBySlug(slug, parents.size() > 0 ? parents[-1].id : null);

        return collection
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
