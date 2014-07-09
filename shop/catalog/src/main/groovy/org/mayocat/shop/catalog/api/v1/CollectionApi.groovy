/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.api.v1

import com.google.common.base.Strings
import com.yammer.metrics.annotation.Timed
import groovy.transform.CompileStatic
import org.mayocat.Slugifier
import org.mayocat.attachment.MetadataExtractor
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.cms.news.model.Article
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.model.Attachment
import org.mayocat.model.Entity
import org.mayocat.model.EntityAndCount
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.api.delegate.EntityApiDelegateHandler
import org.mayocat.rest.api.delegate.ImageGalleryApiDelegate
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.rest.api.object.LinkApiObject
import org.mayocat.rest.api.object.Pagination
import org.mayocat.shop.catalog.CatalogService
import org.mayocat.rest.api.delegate.AttachmentApiDelegate
import org.mayocat.shop.catalog.api.v1.object.*
import org.mayocat.shop.catalog.store.CollectionStore
import org.mayocat.store.*
import org.mayocat.attachment.store.AttachmentStore
import org.slf4j.Logger
import org.xwiki.component.annotation.Component
import org.xwiki.component.phase.Initializable

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * REST API for collections of products.
 *
 * @version $Id$
 */
@Component("/api/collections")
@Path("/api/collections")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
@CompileStatic
class CollectionApi implements Resource, Initializable
{
    @Inject
    EntityDataLoader dataLoader

    @Inject
    Provider<CollectionStore> collectionStore

    @Inject
    Provider<EntityListStore> entityListStore

    @Inject
    CatalogService catalogService

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

    // Entity handler for delegates

    EntityApiDelegateHandler collectionHandler = new EntityApiDelegateHandler() {
        Entity getEntity(String slug)
        {
            return collectionStore.get().findBySlug(slug)
        }

        void updateEntity(Entity entity)
        {
            collectionStore.get().update(entity)
        }

        String type()
        {
            "collection"
        }
    }

    void initialize()
    {
        attachmentApi = new AttachmentApiDelegate([
                extractors: extractors,
                attachmentStore: attachmentStore,
                slugifier: slugifier,
                handler: collectionHandler,
                doAfterAttachmentAdded: { String target, Entity entity, String fileName, Attachment created ->
                    switch (target) {
                        case "image-gallery":
                            afterImageAddedToGallery(entity as org.mayocat.shop.catalog.model.Collection, fileName, created)
                            break;
                    }
                }
        ])
        imageGalleryApi = new ImageGalleryApiDelegate([
                dataLoader: dataLoader,
                attachmentStore: attachmentStore.get(),
                entityListStore: entityListStore.get(),
                handler: collectionHandler
        ])
    }


    @GET
    @Timed
    def getAllCollections(@QueryParam("number") @DefaultValue("50") Integer number,
                          @QueryParam("offset") @DefaultValue("0") Integer offset,
                          @QueryParam("expand") @DefaultValue("") String expand)
    {
        List<CollectionApiObject> collectionList = [];

        if (expand.equals("productCount")) {
            List<EntityAndCount<org.mayocat.shop.catalog.model.Collection>> entityAndCount =
                catalogService.findAllCollectionsWithProductCount()

            entityAndCount.each({ EntityAndCount<org.mayocat.shop.catalog.model.Collection> eac ->
                CollectionApiObject apiObject = new CollectionApiObject([
                        _href: "/api/collections/${eac.entity.slug}"
                ])
                apiObject.withCollection(eac.entity)
                apiObject.withProductCount(eac.count)
                collectionList << apiObject
            })
        } else {
            collectionList = this.catalogService.findAllCollections(number, offset).collect({
                org.mayocat.shop.catalog.model.Collection collection ->
                    CollectionApiObject apiObject = new CollectionApiObject([
                            _href: "/api/collections/${collection.slug}"
                    ])
                    apiObject.withCollection(collection)
                    apiObject
            })
        }

        def collectionListResult = new CollectionListApiObject([
                _pagination: new Pagination([
                        numberOfItems: number,
                        returnedItems: collectionList.size(),
                        offset: offset,
                        totalItems: collectionStore.get().countAll(),
                        urlTemplate: '/api/collections?number=${numberOfItems}&offset=${offset}',
                ]),
                collections: collectionList
        ])

        collectionListResult
    }

    @Path("{slug}")
    @GET
    @Timed
    def Object getCollection(@PathParam("slug") String slug, @QueryParam("expand") @DefaultValue("") String expand)
    {
        org.mayocat.shop.catalog.model.Collection collection = this.catalogService.findCollectionBySlug(slug);

        EntityData<org.mayocat.shop.catalog.model.Collection> collectionData = dataLoader.load(collection)

        def gallery = collectionData.getData(ImageGallery)
        List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

        if (collection == null) {
            return Response.status(404).build();
        }

        CollectionApiObject collectionApiObject = new CollectionApiObject([
            _href: "/api/products/${slug}/",
            _links: [
                    self: new LinkApiObject([ href: "/api/collections/${slug}/" ]),
                    images: new LinkApiObject([ href: "/api/collections/${slug}/images" ])
            ]
        ])

        collectionApiObject.withCollection(collection)
        collectionApiObject.withEmbeddedImages(images, collection.featuredImageId)

        if (!Strings.isNullOrEmpty(expand)) {
            collectionApiObject.withProductRelationships(this.catalogService.findProductsForCollection(collection))
        }

        collectionApiObject
    }

    @Path("{slug}/move")
    @POST
    @Timed
    @Authorized
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.WILDCARD)
    public Response move(@PathParam("slug") String slug,
            @FormParam("before") String slugOfCollectionToMoveBeforeOf,
            @FormParam("after") String slugOfCollectionToMoveAfterOf)
    {
        try {
            if (!Strings.isNullOrEmpty(slugOfCollectionToMoveAfterOf)) {
                this.catalogService.moveCollection(slug,
                        slugOfCollectionToMoveAfterOf, CatalogService.InsertPosition.AFTER);
            } else {
                this.catalogService.moveCollection(slug, slugOfCollectionToMoveBeforeOf);
            }

            return Response.noContent().build();

        } catch (InvalidMoveOperation e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid move operation").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
    }

    @Path("{slug}/addProduct")
    @POST
    @Timed
    @Authorized
    @Consumes([MediaType.APPLICATION_FORM_URLENCODED, MediaType.TEXT_PLAIN])
    @Produces(MediaType.WILDCARD)
    // TODO
    // A better approach would be to have a POST {slug}/elements for adding new products to a collection
    // a DELETE {slug}/elements/{elementSlug} for removing
    // and a POST {slug}/elements/{elementSlug} for moving position
    public Response addProduct(@PathParam("slug") String slug, @FormParam("product") String product) {
        try {
            this.catalogService.addProductToCollection(slug, product);
            return Response.noContent().build();
        } catch (InvalidOperation e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid operation").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
    }


    @Path("{slug}/removeProduct")
    @POST
    @Timed
    @Authorized
    @Consumes([MediaType.APPLICATION_FORM_URLENCODED, MediaType.TEXT_PLAIN])
    @Produces(MediaType.WILDCARD)
    public Response removeProduct(@PathParam("slug") String slug, @FormParam("product") String product) {
        try {
            this.catalogService.removeProductFromCollection(slug, product);
            return Response.noContent().build();
        } catch (InvalidOperation e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid operation").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
    }

    @Path("{slug}/moveProduct")
    @POST
    @Timed
    @Authorized
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.WILDCARD)
    public Response moveProduct(@PathParam("slug") String slug,
            @FormParam("product") String slugOfProductToMove, @FormParam("before") String slugOfProductToMoveBeforeOf,
            @FormParam("after") String slugOfProductToMoveAfterOf)
    {
        try {
            org.mayocat.shop.catalog.model.Collection collection = this.catalogService.findCollectionBySlug(slug);

            if (!Strings.isNullOrEmpty(slugOfProductToMoveAfterOf)) {
                this.catalogService.moveProductInCollection(collection, slugOfProductToMove,
                        slugOfProductToMoveAfterOf, CatalogService.InsertPosition.AFTER);
            } else {
                this.catalogService.moveProductInCollection(collection, slugOfProductToMove,
                        slugOfProductToMoveBeforeOf);
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
    public Response updateCollection(@PathParam("slug") String slug,
            CollectionApiObject collectionApiObject)
    {
        try {
            org.mayocat.shop.catalog.model.Collection collection = this.collectionStore.get().findBySlug(slug);
            if (collection == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No collection with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
            } else {

                def id = collection.id
                def featuredImageId = collection.featuredImageId
                collection = collectionApiObject.toCollection()

                // ID and slugs are not update-able
                collection.id = id
                collection.slug = slug

                // Featured image is updated via the /images API only, set it back
                collection.featuredImageId = featuredImageId

                this.catalogService.updateCollection(collection);

                return Response.ok().build();
            }

        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.message, e.errors);
        }
    }

    @Path("{slug}")
    @PUT
    @Timed
    @Authorized
    public Response replaceCollection(@PathParam("slug") String slug,
            org.mayocat.shop.catalog.model.Collection newCollection)
    {
        // TODO
        throw new RuntimeException("Not implemented");
    }

    @Path("{slug}")
    @DELETE
    @Authorized
    @Consumes(MediaType.WILDCARD)
    public Response deleteCollection(@PathParam("slug") String slug)
    {
        try {
            this.catalogService.deleteCollection(slug);

            return Response.noContent().build();
        }
        catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No collection with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @POST
    @Timed
    @Authorized
    public Response createCollection(CollectionApiObject collection)
    {
        try {
            def coll = collection.toCollection();

            // Set slug TODO: verify if provided slug is conform
            coll.slug = Strings.isNullOrEmpty(collection.slug) ? slugifier.slugify(coll.title) : collection.slug

            org.mayocat.shop.catalog.model.Collection created =
                this.catalogService.createCollection(coll);


            // Respond with a created URI relative to this API URL.
            // This will add a location header like http://host/api/<version>/collection/my-created-collection
            return Response.created(new URI(created.slug)).build();

        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.message, e.errors);
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("A Collection with this slug already exists\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e);
        }
    }
}
