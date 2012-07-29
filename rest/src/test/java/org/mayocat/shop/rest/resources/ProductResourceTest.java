package org.mayocat.shop.rest.resources;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.MediaType;

import org.junit.Ignore;
import org.junit.Test;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.ProductStore;

import com.sun.jersey.api.client.ClientResponse;
import com.yammer.dropwizard.testing.ResourceTest;

public class ProductResourceTest extends ResourceTest
{

    private final Product product = new Product();
    
    private final ProductStore store = mock(ProductStore.class);
    
    @Override
    protected void setUpResources() throws Exception
    {
        product.setHandle("handle");
        when(store.getProduct(anyString(), anyString())).thenReturn(product);
        //when(store.persist(anyString(), product)).thenReturn(product);
        addResource(new ProductResource(store));
    }

    @Test
    public void testGetProduct() throws Exception {
        assertThat("GET requests fetch the Product by handle",
                   client().resource("/product/handle").get(Product.class),
                   is(product));

        verify(store).getProduct("tenant", "handle");
    }
    
    @Test
    @Ignore
    public void testPutRequestRequireValidProduct() throws Exception {
        ClientResponse cr =  client().resource("/product/")
                                     .type(MediaType.APPLICATION_JSON)
                                     .entity("{\"handle\":\"fdsa\", \"tenant\":\"test\"}")
                                     .put(ClientResponse.class); 
        
        // FIXME This leads to a  org.codehaus.jackson.map.JsonMappingException: Can not instantiate value of type [simple type, class org.mayocat.shop.model.Product] 
        // from JSON String; no single-String constructor/factory method.
        // However, PUTing product representations when running the app "normally" works just fine.
        
        //verify(store).persist("tenant", product);
    }
}
