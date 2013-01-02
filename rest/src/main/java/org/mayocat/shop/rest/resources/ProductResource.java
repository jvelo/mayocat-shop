package org.mayocat.shop.rest.resources;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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
import org.mayocat.shop.model.Product;
import org.mayocat.shop.rest.representations.ProductRepresentation;
import org.mayocat.shop.service.CatalogService;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.yammer.metrics.annotation.Timed;

@Component("ProductResource")
@Path("/product/")
public class ProductResource implements Resource
{
    @Inject
    private CatalogService catalogueService;

    @Inject
    private Logger logger;

    @GET
    @Timed
    @Produces({"application/json; charset=UTF-8"})
    public List<ProductRepresentation> getAllProducts(@Authorized Context context,
        @QueryParam("number") @DefaultValue("50") Integer number,
        @QueryParam("offset") @DefaultValue("0") Integer offset)
    {
        try {
            return this.wrapInReprensentations(this.catalogueService.findAllProducts(number, offset));

        } catch (StoreException e) {
            this.logger.error("Error while getting products", e);
            throw new WebApplicationException(e);
        }
    }

    @Path("{slug}")
    @GET
    @Timed
    @Produces({"application/json; charset=UTF-8"})
    public Object getProduct(@Authorized Context context, @PathParam("slug") String slug)
    {
        try {
            Product product = this.catalogueService.findProductBySlug(slug);
            if (product == null) {
                return Response.status(404).build();
            }
            return this.wrapInRepresentation(product);
        } catch (StoreException e) {
            this.logger.error("Error while getting product", e);
            throw new WebApplicationException(e);
        }
    }

    @Path("{slug}")
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProduct(@Authorized Context context, @PathParam("slug") String slug,
        Product updatedProduct)
    {
        try {
            Product product = this.catalogueService.findProductBySlug(slug);
            if (product == null) {
                return Response.status(404).build();
            } else {
                this.catalogueService.updateProduct(updatedProduct);
            }

            return Response.ok().build();

        } catch (StoreException e) {
            this.logger.error("Error while updating product", e);
            throw new WebApplicationException(e);
        } catch (InvalidEntityException e) {
            this.logger.error("Error while updating product: invalid entity", e);
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        }
    }

    @Path("{slug}")
    @PUT
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response replaceProduct(@Authorized Context context, @PathParam("slug") String slug, Product newProduct)
    {
        // TODO
        throw new RuntimeException("Not implemented");
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProduct(@Authorized(value = AddProduct.class) Context context, Product product)
    {
        try {
            this.catalogueService.createProduct(product);

            return Response.ok().build();
        } catch (StoreException e) {
            this.logger.error("Error while creating product", e);
            throw new WebApplicationException(e);
        } catch (InvalidEntityException e) {
            this.logger.error("Error while creating product: invalid entity", e);
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityAlreadyExistsException e) {
            this.logger.error("Error while creating product: entity already exists", e);
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT)
                .entity("A product with this slug already exists\n").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<ProductRepresentation> wrapInReprensentations(List<Product> products)
    {
        List<ProductRepresentation> result = new ArrayList<ProductRepresentation>();
        for (Product product : products) {
            result.add(this.wrapInRepresentation(product));
        }
        return result;
    }

    private ProductRepresentation wrapInRepresentation(Product product)
    {
        return new ProductRepresentation(product);
    }
}
