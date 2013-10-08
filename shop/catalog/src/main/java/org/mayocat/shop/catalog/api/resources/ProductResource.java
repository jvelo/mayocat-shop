package org.mayocat.shop.catalog.api.resources;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.attachment.util.AttachmentUtils;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Addon;
import org.mayocat.model.Attachment;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.representations.EntityReferenceRepresentation;
import org.mayocat.rest.representations.ImageRepresentation;
import org.mayocat.rest.resources.AbstractAttachmentResource;
import org.mayocat.rest.support.AddonsRepresentationUnmarshaller;
import org.mayocat.shop.catalog.api.representations.ProductRepresentation;
import org.mayocat.shop.catalog.meta.ProductEntity;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.CollectionStore;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.HasOrderedCollections;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.InvalidMoveOperation;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.yammer.metrics.annotation.Timed;

@Component(ProductResource.PATH)
@Path(ProductResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
public class ProductResource extends AbstractAttachmentResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + ProductEntity.PATH;

    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private Provider<CollectionStore> collectionStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private AddonsRepresentationUnmarshaller addonsRepresentationUnmarshaller;

    @Inject
    private Logger logger;

    @GET
    @Timed
    @Authorized
    public List<ProductRepresentation> getProducts(
            @QueryParam("number") @DefaultValue("50") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset,
            @QueryParam("filter") @DefaultValue("") String filter)
    {
        if (filter.equals("uncategorized")) {
            return this.wrapInRepresentations(this.productStore.get().findOrphanProducts());
        } else {
            return this.wrapInRepresentations(this.productStore.get().findAll(number, offset));
        }
    }

    @Path("{slug}")
    @GET
    @Timed
    @Authorized
    public Object getProduct(@PathParam("slug") String slug,
            @QueryParam("expand") @DefaultValue("") List<String> expansions)
    {
        Product product = this.productStore.get().findBySlug(slug);
        if (product == null) {
            return Response.status(404).build();
        }

        if (expansions.contains("collections")) {
            List<Collection> collections = this.collectionStore.get().findAllForProduct(product);
            if (expansions.contains("images")) {
                return this.wrapInRepresentation(product, collections, this.getImages(slug));
            } else {
                return this.wrapInRepresentation(product, collections, null);
            }
        } else if (expansions.contains("images")) {
            return this.wrapInRepresentation(product, this.getImages(slug));
        } else {
            return this.wrapInRepresentation(product);
        }
    }

    @Path("{slug}/images")
    @GET
    public List<ImageRepresentation> getImages(@PathParam("slug") String slug)
    {
        List<ImageRepresentation> result = new ArrayList();
        Product product = this.productStore.get().findBySlug(slug);
        if (product == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        for (Attachment attachment : this.getAttachmentStore().findAllChildrenOf(product,
                Arrays.asList("png", "jpg", "jpeg", "gif")))
        {
            List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
            Image image = new Image(attachment, thumbnails);
            ImageRepresentation representation = new ImageRepresentation(image);
            if (product.getFeaturedImageId() != null) {
                if (product.getFeaturedImageId().equals(attachment.getId())) {
                    representation.setFeatured(true);
                }
            }

            result.add(representation);
        }

        return result;
    }

    @Path("{slug}/images/{imageSlug}")
    @DELETE
    @Consumes(MediaType.WILDCARD)
    public Response detachImage(@PathParam("slug") String slug, @PathParam("imageSlug") String imageSlug)
    {
        Attachment attachment = getAttachmentStore().findBySlug(imageSlug);
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
    @POST
    @Consumes(MediaType.WILDCARD)
    public Response updateImage(@PathParam("slug") String slug, @PathParam("imageSlug") String imageSlug,
            ImageRepresentation image)
    {
        Attachment attachment = getAttachmentStore().findBySlug(imageSlug);
        if (attachment == null) {
            return Response.status(404).build();
        }
        try {
            attachment.setTitle(image.getTitle());
            attachment.setDescription(image.getDescription());
            getAttachmentStore().update(attachment);
            return Response.noContent().build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(404).build();
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    @Path("{slug}/attachments")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addAttachment(@PathParam("slug") String slug,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("title") String title, @FormDataParam("description") String description)
    {
        Product product = this.productStore.get().findBySlug(slug);
        if (product == null) {
            return Response.status(404).build();
        }

        Attachment created = this.addAttachment(uploadedInputStream, fileDetail.getFileName(), title, description,
                Optional.of(product.getId()));

        if (product.getFeaturedImageId() == null && AttachmentUtils.isImage(fileDetail.getFileName())
                && created != null) {

            // If this is an image and the product doesn't have a featured image yet, and the attachment was
            // successful, the we set this image as featured image.
            for (Attachment attachment : this.getAttachmentStore().findAllChildrenOf(product,
                    Arrays.asList("png", "jpg", "jpeg", "gif")))
            {
                product.setFeaturedImageId(attachment.getId());
                break;
            }
            try {
                this.productStore.get().update(product);
            } catch (EntityDoesNotExistException e) {
                // Fail silently. The attachment has been added successfully, that's what matter
            } catch (InvalidEntityException e) {
                // Fail silently. The attachment has been added successfully, that's what matter
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
    public Response move(@PathParam("slug") String slug,
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
    // Partial update : NOT idempotent
    public Response updateProduct(@PathParam("slug") String slug,
            @Valid ProductRepresentation updatedProductRepresentation)
    {
        try {
            Product product = this.productStore.get().findBySlug(slug);
            if (product == null) {
                return Response.status(404).build();
            } else {
                product.setId(product.getId());
                product.setTitle(updatedProductRepresentation.getTitle());
                product.setDescription(updatedProductRepresentation.getDescription());
                product.setModel(updatedProductRepresentation.getModel());
                product.setOnShelf(updatedProductRepresentation.getOnShelf());
                product.setPrice(updatedProductRepresentation.getPrice());
                product.setWeight(updatedProductRepresentation.getWeight());
                product.setStock(updatedProductRepresentation.getStock());
                product.setLocalizedVersions(updatedProductRepresentation.getLocalizedVersions());
                product.setAddons(addonsRepresentationUnmarshaller.unmarshall(updatedProductRepresentation.getAddons()));

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
    public Response deleteProduct(@PathParam("slug") String slug)
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
    public Response replaceProduct(@PathParam("slug") String slug, Product newProduct)
    {
        // TODO
        throw new RuntimeException("Not implemented");
    }

    @POST
    @Timed
    @Authorized //(roles = { Role.ADMIN })
    public Response createProduct(@Valid ProductRepresentation productRepresentation)
    {
        try {
            Product product = new Product();
            product.setSlug(productRepresentation.getSlug());
            product.setTitle(productRepresentation.getTitle());

            if (Strings.isNullOrEmpty(product.getSlug())) {
                product.setSlug(this.getSlugifier().slugify(product.getTitle()));
            }

            product.setModel(productRepresentation.getModel());
            product.setDescription(productRepresentation.getDescription());
            product.setOnShelf(productRepresentation.getOnShelf());
            product.setPrice(productRepresentation.getPrice());
            product.setStock(productRepresentation.getStock());

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

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<ProductRepresentation> wrapInRepresentations(List<Product> products)
    {
        List<ProductRepresentation> result = new ArrayList<ProductRepresentation>();
        for (Product product : products) {
            result.add(this.wrapInRepresentation(product));
        }
        return result;
    }

    private ProductRepresentation wrapInRepresentation(Product product)
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

    private ProductRepresentation wrapInRepresentation(Product product,
            List<ImageRepresentation> images)
    {
        ProductRepresentation result = new ProductRepresentation(product);
        if (images != null) {
            result.setImages(images);
            for (ImageRepresentation image : images) {
                if (image.isFeaturedImage()) {
                    result.setFeaturedImage(image);
                }
            }
        }
        if (product.getAddons().isLoaded()) {
            List<AddonRepresentation> addons = Lists.newArrayList();
            for (Addon a : product.getAddons().get()) {
                addons.add(new AddonRepresentation(a));
            }
            result.setAddons(addons);
        }
        return result;
    }

    private ProductRepresentation wrapInRepresentation(Product product, List<Collection> collections,
            List<ImageRepresentation> images)
    {
        List<EntityReferenceRepresentation> collectionsReferences = Lists.newArrayList();
        for (Collection collection : collections) {
            collectionsReferences
                    .add(new EntityReferenceRepresentation(CollectionResource.PATH + "/" + collection.getSlug(),
                            collection.getSlug(), collection.getTitle()
                    ));
        }
        ProductRepresentation result = new ProductRepresentation(product, collectionsReferences);
        if (images != null) {
            result.setImages(images);
            for (ImageRepresentation image : images) {
                if (image.isFeaturedImage()) {
                    result.setFeaturedImage(image);
                }
            }
        }
        if (product.getAddons().isLoaded()) {
            List<AddonRepresentation> addons = Lists.newArrayList();
            for (Addon a : product.getAddons().get()) {
                addons.add(new AddonRepresentation(a));
            }
            result.setAddons(addons);
        }
        return result;
    }
}
