package org.mayocat.shop.catalog.api.v1

import com.google.common.base.Optional
import com.google.common.base.Strings
import com.sun.jersey.core.header.FormDataContentDisposition
import com.sun.jersey.multipart.FormDataParam
import com.yammer.metrics.annotation.Timed
import org.apache.commons.lang3.StringUtils
import org.mayocat.Slugifier
import org.mayocat.attachment.util.AttachmentUtils
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.image.model.Image
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.model.Attachment
import org.mayocat.model.EntityAndCount
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.shop.catalog.CatalogService
import org.mayocat.shop.catalog.api.v1.delegate.AttachmentApiDelegate
import org.mayocat.shop.catalog.api.v1.object.*
import org.mayocat.shop.catalog.store.CollectionStore
import org.mayocat.store.*
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
class CollectionApi implements Resource, Initializable
{
    @Inject
    Provider<CollectionStore> collectionStore

    @Inject
    CatalogService catalogService

    @Inject
    Provider<AttachmentStore> attachmentStore

    @Inject
    Provider<ThumbnailStore> thumbnailStore

    @Inject
    Slugifier slugifier

    @Inject
    Logger logger

    @Delegate
    AttachmentApiDelegate attachmentApi

    void initialize() {
        attachmentApi = new AttachmentApiDelegate([
                attachmentStore: attachmentStore,
                slugifier: slugifier
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
                pagination: new Pagination([
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

        def images = this.getImages(slug)

        if (collection == null) {
            return Response.status(404).build();
        }

        CollectionApiObject collectionApiObject = new CollectionApiObject([
            _href: "/api/products/${slug}/",
            _links: [
                    self: new LinkApiObject([ href: "/api/products/${slug}/" ]),
                    images: new LinkApiObject([ href: "/api/products/${slug}/images" ])
            ]
        ])

        collectionApiObject.withCollection(collection)
        collectionApiObject.withEmbeddedImages(images)

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
                collection = collectionApiObject.toCollection()

                // ID and slugs are not update-able
                collection.id = id
                collection.slug = slug

                if (collectionApiObject._embedded.get("featuredImage")) {
                    // FIXME:
                    // This should be done via the {slug}/images/ API instead

                    ImageApiObject featured = collectionApiObject._embedded.get("featuredImage") as ImageApiObject

                    Attachment featuredImage =
                        this.attachmentStore.get().findBySlugAndExtension(featured.slug, featured.file.extension);

                    if (featuredImage) {
                        collection.featuredImageId = featuredImage.id
                    }
                }

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
            org.mayocat.shop.catalog.model.Collection created =
                this.catalogService.createCollection(collection.toCollection());

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

    @Path("{slug}/images")
    @GET
    def getImages(@PathParam("slug") String slug)
    {
        def images = [];
        def collection = this.collectionStore.get().findBySlug(slug);
        if (collection == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        for (Attachment attachment : this.attachmentStore.get().findAllChildrenOf(collection,
                ["png", "jpg", "jpeg", "gif"] as List))
        {
            def thumbnails = thumbnailStore.get().findAll(attachment);
            def image = new Image(attachment, thumbnails);

            def imageApiObject = new ImageApiObject()
            imageApiObject.withImage(image)

            if (collection.featuredImageId != null && collection.featuredImageId.equals(attachment.id)) {
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
        def attachment = attachmentStore.get().findBySlug(imageSlug);
        if (attachment == null) {
            return Response.status(404).build();
        }
        try {
            attachmentStore.get().detach(attachment);
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
            @PathParam("imageSlug") String imageSlug, ImageApiObject image)
    {
        def attachment = attachmentStore.get().findBySlug(imageSlug);
        if (attachment == null) {
            return Response.status(404).build();
        }
        try {
            attachment.with {
                setTitle image.title
                setDescription image.description
                setLocalizedVersions image._localized
            }
            attachmentStore.get().update(attachment);
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
            @FormDataParam("filename") String sentFilename,
            @FormDataParam("title") String title,
            @FormDataParam("description") String description)
    {
        def collection = this.collectionStore.get().findBySlug(slug);
        if (collection == null) {
            return Response.status(404).build();
        }

        def filename = StringUtils.defaultIfBlank(fileDetail.fileName, sentFilename) as String;
        def created = this.addAttachment(uploadedInputStream, filename, title, description,
                Optional.of(collection.id));

        if (collection.featuredImageId == null && AttachmentUtils.isImage(filename) && created != null) {

            // If this is an image and the product doesn't have a featured image yet, and the attachment was
            // successful, the we set this image as featured image.
            collection.featuredImageId = created.id;

            try {
                this.collectionStore.get().update(collection);
            } catch (EntityDoesNotExistException | InvalidEntityException e) {
                // Fail silently. The attachment has been added successfully, that's what matter
                this.logger.warn("Failed to set first image as featured image for entity {} with id", collection.getId());
            }
        }

        return Response.noContent().build();
    }
}
