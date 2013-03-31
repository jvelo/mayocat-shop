package org.mayocat.shop.catalog.api.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
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

import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.shop.catalog.api.representations.CollectionRepresentation;
import org.mayocat.shop.catalog.meta.CollectionEntity;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.model.EntityAndCount;
import org.mayocat.accounts.model.Role;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.representations.EntityReferenceRepresentation;
import org.mayocat.rest.Resource;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.InvalidOperation;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.yammer.metrics.annotation.Timed;

@Component(CollectionResource.PATH)
@Path(CollectionResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
public class CollectionResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + CollectionEntity.PATH;

    @Inject
    private CatalogService catalogService;

    @Inject
    private Logger logger;

    @GET
    @Timed
    @Authorized
    public List<CollectionRepresentation> getAllCollections(
            @QueryParam("number") @DefaultValue("50") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset,
            @QueryParam("expand") @DefaultValue("") String expand)
    {
        // FIXME support by default infinite number

        if (expand.equals("productCount")) {
            return this.wrapInRepresentationsWithCount(
                    this.catalogService.findAllCollectionsWithProductCount());
        }
        //else if (expand.equals("products")) {
             // TODO
        //}
        else {
            return this.wrapInRepresentations(this.catalogService.findAllCollections(number, offset));
        }
    }

    @Path("{slug}")
    @GET
    @Timed
    @Authorized
    public Object getCollection(@PathParam("slug") String slug, @QueryParam("expand") @DefaultValue("") String expand)
    {
        Collection collection = this.catalogService.findCollectionBySlug(slug);
        if (collection == null) {
            return Response.status(404).build();
        }
        if (!Strings.isNullOrEmpty(expand)) {
            List<Product> products = this.catalogService.findProductsForCollection(collection);
            return this.wrapInRepresentation(collection, products);
        } else {
            return this.wrapInRepresentation(collection);
        }
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
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.TEXT_PLAIN})
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
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.TEXT_PLAIN})
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
            Collection collection = this.catalogService.findCollectionBySlug(slug);

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
            Collection updatedCollection)
    {
        try {
            this.catalogService.updateCollection(updatedCollection);

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No collection with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @Path("{slug}")
    @PUT
    @Timed
    @Authorized
    public Response replaceCollection(@PathParam("slug") String slug,
            Collection newCollection)
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
    @Authorized(roles = { Role.ADMIN })
    public Response createCollection(Collection collection)
    {
        try {
            Collection created = this.catalogService.createCollection(collection);

            // Respond with a created URI relative to this API URL.
            // This will add a location header like http://host/api/<version>/collection/my-created-collection
            return Response.created(new URI(created.getSlug())).build();

        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                .entity("A Collection with this slug already exists\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<CollectionRepresentation> wrapInRepresentationsWithCount(List<EntityAndCount<Collection>> collections)
    {
        List<CollectionRepresentation> result = new ArrayList<CollectionRepresentation>();
        for (EntityAndCount<Collection> entity : collections) {
            result.add(this.wrapInRepresentation(entity.getEntity(), entity.getCount()));
        }
        return result;
    }

    private List<CollectionRepresentation> wrapInRepresentations(List<Collection> collections)
    {
        List<CollectionRepresentation> result = new ArrayList<CollectionRepresentation>();
        for (Collection collection : collections) {
            result.add(this.wrapInRepresentation(collection));
        }
        return result;
    }

    private CollectionRepresentation wrapInRepresentation(Collection collection)
    {
        return new CollectionRepresentation(collection);
    }

    private CollectionRepresentation wrapInRepresentation(Collection collection, List<Product> products)
    {
        List<EntityReferenceRepresentation> collectionsReferences = Lists.newArrayList();
        for (Product product : products) {
            collectionsReferences.add(new EntityReferenceRepresentation(product.getTitle(), product.getSlug(),
                    "/api/1.0/product/" + product.getSlug()
            ));
        }
        return new CollectionRepresentation(collection, collectionsReferences);
    }

    private CollectionRepresentation wrapInRepresentation(Collection collection, Long count)
    {
        return new CollectionRepresentation(count, collection);
    }
}
