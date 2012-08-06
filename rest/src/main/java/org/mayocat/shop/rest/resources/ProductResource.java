package org.mayocat.shop.rest.resources;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.shop.authorization.Context;
import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.authorization.capability.shop.AddProduct;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.ProductStore;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

import com.yammer.metrics.annotation.Timed;

@Component("ProductResource")
@Path("/product/")
public class ProductResource implements Resource
{
    @Inject
    private Provider<ProductStore> store;

    @Path("{handle}")
    @GET
    @Timed
    @Produces({"application/json; charset=UTF-8"})
    public Object getProduct(@Authorized Context context, @PathParam("handle") String handle)
    {
        try {
            Product product = this.store.get().findByHandle(handle);
            if (product == null) {
                return Response.status(404).build();
            }
            return product;
        } catch (StoreException e) {
            throw new WebApplicationException(e);
        }
    }

    @PUT
    @Timed
    public Response createProduct(@Authorized(value = AddProduct.class) Context context,
        Product product)
    {
        try {
            this.store.get().create(product);

            return Response.ok().build();
        } catch (StoreException e) {
            throw new WebApplicationException(e);
        } catch (EntityAlreadyExistsException e) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT)
                .entity("A product with this handle already exists").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
    }
}
