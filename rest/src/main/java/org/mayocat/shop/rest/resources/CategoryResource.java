package org.mayocat.shop.rest.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.authorization.capability.shop.AddProduct;
import org.mayocat.shop.context.Context;
import org.mayocat.shop.model.Category;
import org.mayocat.shop.service.CatalogService;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

import com.yammer.metrics.annotation.Timed;

@Component("CategoryResource")
@Path("/category/")
public class CategoryResource implements Resource
{

    @Inject
    private CatalogService catalogService;

    @Path("{handle}")
    @GET
    @Timed
    @Produces({"application/json; charset=UTF-8"})
    public Object getCategory(@Authorized Context context, @PathParam("handle") String handle)
    {
        try {
            Category category = this.catalogService.findCategoryByHandle(handle);
            if (category == null) {
                return Response.status(404).build();
            }
            return category;
        } catch (StoreException e) {
            throw new WebApplicationException(e);
        }
    }

    @Path("{handle}/move")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response changePosition(@Authorized Context context, @PathParam("handle") String handle,
        @FormParam("product") String handleOfProductToMove, @FormParam("before") String handleOfProductToMoveBeforeOf)
    {
        try {
            Category category = this.catalogService.findCategoryByHandle(handle);
            this.catalogService.moveProductInCategory(category, handleOfProductToMove, handleOfProductToMoveBeforeOf);
            return Response.ok().build();
        }
        catch (StoreException e) {
            throw new WebApplicationException(e);
        }
    }

    @Path("{handle}")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCategory(@Authorized Context context, @PathParam("handle") String handle,
        Category updatedCategory)
    {
        try {
            Category category = this.catalogService.findCategoryByHandle(handle);
            if (category == null) {
                return Response.status(404).build();
            } else {
                this.catalogService.updateCategory(updatedCategory);
            }

            return Response.ok().build();

        } catch (StoreException e) {
            throw new WebApplicationException(e);
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        }
    }

    @Path("{handle}")
    @PUT
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response replaceCategory(@Authorized Context context, @PathParam("handle") String handle,
        Category newCategory)
    {
        // TODO
        throw new RuntimeException("Not implemented");
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCategory(@Authorized(value = AddProduct.class) Context context, Category category)
    {
        try {
            this.catalogService.createCategory(category);

            return Response.ok().build();
        } catch (StoreException e) {
            throw new WebApplicationException(e);
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityAlreadyExistsException e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT)
                .entity("A Category with this handle already exists").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
    }
}
