package org.mayocat.shop.rest.resources;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.multitenancy.QueryTenant;
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
    public Object search(@PathParam("handle") String handle, @QueryTenant Tenant tenant)
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
    public Response createProduct(Product product, @QueryTenant Tenant tenant)
    {
        try {
            // product.setTenant(tenant);
            this.store.get().create(product);

            return Response.ok().build();
        } catch (StoreException e) {
            throw new WebApplicationException(e);
        }
    }
}
