package org.mayocat.shop.rest.resources;

import org.junit.Test;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.ProductStore;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;

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
        addResource(new ProductResource(store));
    }

    @Test
    public void getGetProduct() throws Exception {
        assertThat("GET requests fetch the Product by handle",
                   client().resource("/product/handle").get(Product.class),
                   is(product));

        verify(store).getProduct("tenant", "handle");
    }
    
}
