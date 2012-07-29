package org.mayocat.shop.rest.resources;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.ProductStore;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

import com.yammer.metrics.annotation.Timed;

@Component("ProductResource")
@Path("/product/")
public class ProductResource implements Resource
{
    @Inject
    private ProductStore store;

    /**
     * Testing constructor.
     */
    public ProductResource(ProductStore store)
    {
        this.store = store;
    }

    @Path("{handle}")
    @GET
    @Timed
    @Produces({"application/json; charset=UTF-8"})
    public Object search(@PathParam("handle") String handle, @Context UriInfo uriInfo)
    {
        try {
            String host = uriInfo.getBaseUri().getHost();

            Product product = this.store.getProduct("tenant", handle);
            if (product == null) {
                return Response.status(404  ).build();
            }
            return product;
        } catch (StoreException e) {
            throw new WebApplicationException(e);
        }
    }

    @PUT
    public Response createProduct(@Valid Product product)
    {
        try {
            this.store.persist("tenant", product);

            return Response.noContent().build();
        } catch (StoreException e) {
            throw new WebApplicationException(e);
        }
    }
}
