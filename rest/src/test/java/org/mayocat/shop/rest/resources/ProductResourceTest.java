package org.mayocat.shop.rest.resources;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.MediaType;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.rest.provider.tenant.DefaultTenantResolver;
import org.mayocat.shop.rest.provider.tenant.QueryTenantProvider;
import org.mayocat.shop.store.ProductStore;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.annotation.MockingRequirement;

import com.sun.jersey.api.client.ClientResponse;

@ComponentList(DefaultTenantResolver.class)
public class ProductResourceTest extends AbstractMockingComponentResourceTest
{
    private final Product product = new Product();
    
    private ProductStore store;
    
    @MockingRequirement
    private ProductResource productResource;
    
    @MockingRequirement(
        // Excluding all providers.
        // FIXME : find a way to exclude specifically Provider<TenantResolver> ?
        exceptions=javax.inject.Provider.class
    )
    private QueryTenantProvider tenantProvider;
    
    @Override
    protected void setUpResources() throws Exception
    {
        
        this.store = getComponentManager().getInstance(ProductStore.class);
        
        product.setHandle("handle");
        
        getMockery().checking(new Expectations()        
        {
            {        
                allowing(store).findByTenantAndHandle(with(any(Tenant.class)), with(any(String.class)));
                will(returnValue(product));
            }
        });
        
        addResource(this.tenantProvider);
        addResource(this.productResource);
    }

    @Test
    public void testGetProduct() throws Exception {
        Product returned = client().resource("/product/handle").get(Product.class);
        assertEquals(returned, product);
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
