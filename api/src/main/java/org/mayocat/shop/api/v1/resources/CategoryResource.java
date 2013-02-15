package org.mayocat.shop.api.v1.resources;

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

import org.mayocat.shop.api.v1.representations.CategoryRepresentation;
import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.EntityAndCount;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Role;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.mayocat.shop.rest.representations.EntityReferenceRepresentation;
import org.mayocat.shop.rest.resources.Resource;
import org.mayocat.shop.service.CatalogService;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.EntityDoesNotExistException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.InvalidMoveOperation;
import org.mayocat.shop.store.InvalidOperation;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.yammer.metrics.annotation.Timed;

@Component("/api/1.0/category/")
@Path("/api/1.0/category/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
public class CategoryResource implements Resource
{
    @Inject
    private CatalogService catalogService;

    @Inject
    private Logger logger;

    @GET
    @Timed
    @Authorized
    public List<CategoryRepresentation> getAllCategories(
            @QueryParam("number") @DefaultValue("50") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset,
            @QueryParam("expand") @DefaultValue("") String expand)
    {
        // FIXME support by default infinite number

        if (expand.equals("productCount")) {
            return this.wrapInReprensentationsWithCount(
                    this.catalogService.findAllCategoriesWithProductCount());
        }
        else {
            return this.wrapInReprensentations(this.catalogService.findAllCategories(number, offset));
        }
    }

    @Path("{slug}")
    @GET
    @Timed
    @Authorized
    public Object getCategory(@PathParam("slug") String slug, @QueryParam("expand") @DefaultValue("") String expand)
    {
        Category category = this.catalogService.findCategoryBySlug(slug);
        if (category == null) {
            return Response.status(404).build();
        }
        if (!Strings.isNullOrEmpty(expand)) {
            List<Product> products = this.catalogService.findProductsForCategory(category);
            return this.wrapInRepresentation(category, products);
        } else {
            return this.wrapInRepresentation(category);
        }
    }

    @Path("{slug}/move")
    @POST
    @Timed
    @Authorized
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.WILDCARD)
    public Response move(@PathParam("slug") String slug,
            @FormParam("before") String slugOfCategoryToMoveBeforeOf,
            @FormParam("after") String slugOfCategoryToMoveAfterOf)
    {
        try {
            if (!Strings.isNullOrEmpty(slugOfCategoryToMoveAfterOf)) {
                this.catalogService.moveCategory(slug,
                        slugOfCategoryToMoveAfterOf, CatalogService.InsertPosition.AFTER);
            } else {
                this.catalogService.moveCategory(slug, slugOfCategoryToMoveBeforeOf);
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
    public Response addProduct(@PathParam("slug") String slug, @FormParam("product") String product) {
        try {
            this.catalogService.addProductToCategory(slug, product);
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
            this.catalogService.removeProductFromCategory(slug, product);
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
            Category category = this.catalogService.findCategoryBySlug(slug);

            if (!Strings.isNullOrEmpty(slugOfProductToMoveAfterOf)) {
                this.catalogService.moveProductInCategory(category, slugOfProductToMove,
                        slugOfProductToMoveAfterOf, CatalogService.InsertPosition.AFTER);
            } else {
                this.catalogService.moveProductInCategory(category, slugOfProductToMove,
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
    public Response updateCategory(@PathParam("slug") String slug,
            Category updatedCategory)
    {
        try {
            this.catalogService.updateCategory(updatedCategory);

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No category with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    @Path("{slug}")
    @PUT
    @Timed
    @Authorized
    public Response replaceCategory(@PathParam("slug") String slug,
        Category newCategory)
    {
        // TODO
        throw new RuntimeException("Not implemented");
    }

    @POST
    @Timed
    @Authorized(roles = { Role.ADMIN })
    public Response createCategory(Category category)
    {
        try {
            this.catalogService.createCategory(category);

            return Response.ok().build();
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                .entity("A Category with this slug already exists\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<CategoryRepresentation> wrapInReprensentationsWithCount(List<EntityAndCount<Category>> categories)
    {
        List<CategoryRepresentation> result = new ArrayList<CategoryRepresentation>();
        for (EntityAndCount<Category> entity : categories) {
            result.add(this.wrapInRepresentation(entity.getEntity(), entity.getCount()));
        }
        return result;
    }

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

    private CategoryRepresentation wrapInRepresentation(Category category, List<Product> products)
    {
        List<EntityReferenceRepresentation> categoriesReferences = Lists.newArrayList();
        for (Product product : products) {
            categoriesReferences.add(new EntityReferenceRepresentation(product.getTitle(), "/product/" + product.getSlug()
            ));
        }
        return new CategoryRepresentation(category, categoriesReferences);
    }

    private CategoryRepresentation wrapInRepresentation(Category category, Long count)
    {
        return new CategoryRepresentation(count, category);
    }
}
