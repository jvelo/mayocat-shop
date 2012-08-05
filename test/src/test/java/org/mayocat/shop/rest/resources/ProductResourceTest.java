package org.mayocat.shop.rest.resources;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.junit.Test;
import org.xwiki.test.annotation.AllComponents;

import org.mayocat.shop.model.Product;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

@AllComponents
public class ProductResourceTest extends AbstractResourceTest
{
    ProductResource productResource;

    final Product product = new Product();

    @Override
    protected void setUpResources() throws Exception
    {
        super.setUpResources();
        
        this.productResource = this.componentManager.getInstance(Resource.class, "ProductResource");
        product.setHandle("handle");
        addResource(this.productResource);
    }
    
    @Test
    public void testGetProduct() throws Exception
    {
        Product returned = client().resource("/product/handle").get(Product.class);
        Assert.assertEquals(returned, product);
    }

    @Test
    public void testPutRequestRequireValidProduct() throws Exception
    {
        ClientResponse cr =
            client().resource("/product/").type(MediaType.APPLICATION_JSON).entity("{\"handle\":\"aiya\"}")
                .put(ClientResponse.class);

        Assert.assertEquals(Status.OK, cr.getClientResponseStatus());
    }

}
