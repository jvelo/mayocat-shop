package org.mayocat.shop.rest.resources;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.junit.Test;
import org.xwiki.test.annotation.AllComponents;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

@AllComponents
public class ProductResourceTest extends AbstractAuthenticatedResourceTest
{
    ProductResource productResource;

    @Override
    protected void setUpResources() throws Exception
    {
        super.setUpResources();

        this.productResource = this.componentManager.getInstance(Resource.class, "ProductResource");
        addResource(this.productResource);
    }

    @Test
    public void testGetInexistentProduct() throws Exception
    {
        ClientResponse cr = client().resource("/product/hoverboard")
                                    .header("Authorization", this.getBasicAuthenticationHeader())
                                    .get(ClientResponse.class);

        Assert.assertEquals(Status.NOT_FOUND, cr.getClientResponseStatus());
    }

    @Test
    public void testPutRequestRequireValidProduct() throws Exception
    {
        ClientResponse cr = client().resource("/product/")
                                    .type(MediaType.APPLICATION_JSON)
                                    .entity("{\"handle\":\"aiya\"}")
                                    .header("Authorization", this.getBasicAuthenticationHeader())
                                    .put(ClientResponse.class);

        Assert.assertEquals(Status.OK, cr.getClientResponseStatus());
    }

}
