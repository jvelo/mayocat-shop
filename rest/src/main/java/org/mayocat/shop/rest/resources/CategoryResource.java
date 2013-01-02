package org.mayocat.shop.rest.resources;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
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

import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.authorization.capability.shop.AddProduct;
import org.mayocat.shop.context.Context;
import org.mayocat.shop.model.Category;
import org.mayocat.shop.rest.representations.CategoryRepresentation;
import org.mayocat.shop.service.CatalogService;
import org.mayocat.shop.service.InvalidMoveOperation;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;
import com.yammer.metrics.annotation.Timed;

@Component("CategoryResource")
@Path("/category/")
public class CategoryResource implements Resource
{

    @Inject
    private CatalogService catalogService;

    @Inject
    private Logger logger;

    @GET
    @Timed
    @Produces({"application/json; charset=UTF-8"})
    public List<CategoryRepresentation> getAllCategories(@Authorized Context context,
        @QueryParam("number") @DefaultValue("50") Integer number,
        @QueryParam("offset") @DefaultValue("0") Integer offset)
    {
        try {
            return this.wrapInReprensentations(this.catalogService.findAllCategories(number, offset));
        } catch (StoreException e) {
            this.logger.error("Error while getting categories", e);
            throw new WebApplicationException(e);
        }
    }

    @Path("{slug}")
    @GET
    @Timed
    @Produces({"application/json; charset=UTF-8"})
    public Object getCategory(@Authorized Context context, @PathParam("slug") String slug)
    {
        try {
            Category category = this.catalogService.findCategoryBySlug(slug);
            if (category == null) {
                return Response.status(404).build();
            }
            return this.wrapInRepresentation(category);
        } catch (StoreException e) {
            throw new WebApplicationException(e);
        }
    }

    @Path("{slug}/move")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response changePosition(@Authorized Context context, @PathParam("slug") String slug,
        @FormParam("product") String slugOfProductToMove, @FormParam("before") String slugOfProductToMoveBeforeOf,
        @FormParam("after") String slugOfProductToMoveAfterOf)
    {
        try {
            Category category = this.catalogService.findCategoryBySlug(slug);

            if (!Strings.isNullOrEmpty(slugOfProductToMoveAfterOf)) {
                this.catalogService.moveProductInCategory(category, slugOfProductToMove,
                    slugOfProductToMoveAfterOf, CatalogService.InsertPosition.AFTER);
            } else {
                this.catalogService.moveProductInCategory(category, slugOfProductToMove,
                    slugOfProductToMoveBeforeOf);
            }

            return Response.ok().build();

        } catch (InvalidMoveOperation e) {
            throw new WebApplicationException(e);
        } catch (StoreException e) {
            throw new WebApplicationException(e);
        }
    }

    @Path("{slug}")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCategory(@Authorized Context context, @PathParam("slug") String slug,
        Category updatedCategory)
    {
        try {
            Category category = this.catalogService.findCategoryBySlug(slug);
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

    @Path("{slug}")
    @PUT
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response replaceCategory(@Authorized Context context, @PathParam("slug") String slug,
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
                .entity("A Category with this slug already exists").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<CategoryRepresentation> wrapInReprensentations(List<Category> categories)
    {
        List<CategoryRepresentation> result = new ArrayList<CategoryRepresentation>();
        for (Category category : categories) {
            result.add(this.wrapInRepresentation(category));
        }
        return result;
    }

    private CategoryRepresentation wrapInRepresentation(Category category)
    {
        return new CategoryRepresentation(category);
    }
}
