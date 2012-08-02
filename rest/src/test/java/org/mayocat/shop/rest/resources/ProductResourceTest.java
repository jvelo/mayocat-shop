package org.mayocat.shop.rest.resources;

import static org.junit.Assert.assertEquals;

import javax.inject.Provider;
import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.multitenancy.TenantResolver;
import org.mayocat.shop.multitenancy.jersey.QueryTenantProvider;
import org.mayocat.shop.store.ProductStore;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.util.DefaultParameterizedType;
import org.xwiki.test.annotation.MockingRequirement;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

public class ProductResourceTest extends AbstractMockingComponentResourceTest
{
    @MockingRequirement
    private ProductResource productResource;

    @MockingRequirement
    private QueryTenantProvider queryTenantProvider;

    private final Product product = new Product();

    private Provider<ProductStore> psProvider;

    private ProductStore providedStore;

    private Provider<TenantResolver> trProvider;

    private TenantResolver providedResolver;

    @Override
    protected void setupDependencies() throws Exception
    {
        // Register the provided store
        this.providedStore = getMockery().mock(ProductStore.class, "actual store");
        this.providedResolver = getMockery().mock(TenantResolver.class, "actual resolver");

        DefaultComponentDescriptor<ProductStore> cd = new DefaultComponentDescriptor<ProductStore>();
        cd.setRoleType(ProductStore.class);
        getComponentManager().registerComponent(cd, this.providedStore);

        DefaultComponentDescriptor<TenantResolver> cd2 = new DefaultComponentDescriptor<TenantResolver>();
        cd2.setRoleType(TenantResolver.class);
        getComponentManager().registerComponent(cd2, this.providedResolver);
    }

    @Override
    protected void setUpResources() throws Exception
    {
        addResource(this.queryTenantProvider);
        addResource(this.productResource);
    }

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        DefaultParameterizedType psProviderType =
            new DefaultParameterizedType(Provider.class.getComponentType(), Provider.class, ProductStore.class);
        this.psProvider = this.getComponentManager().getInstance(psProviderType);

        DefaultParameterizedType trProviderType =
            new DefaultParameterizedType(TenantResolver.class.getComponentType(), Provider.class, TenantResolver.class);
        this.trProvider = this.getComponentManager().getInstance(trProviderType);

        this.providedStore = getComponentManager().getInstance(ProductStore.class);

        product.setHandle("handle");

        getMockery().checking(new Expectations()
        {
            {
                // java.reflect.Inject providers...
                allowing(psProvider).get();
                will(returnValue(providedStore));
                allowing(trProvider).get();
                will(returnValue(providedResolver));

                // Mocked components
                allowing(providedStore).findByHandle(with(any(String.class)));
                will(returnValue(product));
                allowing(providedStore).create(with(any(Product.class)));

                allowing(providedResolver).resolve(with(any(String.class)));
                will(returnValue(new Tenant("mytenant")));
            }
        });
    }

    @Test
    public void testGetProduct() throws Exception
    {
        Product returned = client().resource("/product/handle").get(Product.class);
        assertEquals(returned, product);
    }

    @Test
    public void testPutRequestRequireValidProduct() throws Exception
    {
        ClientResponse cr =
            client().resource("/product/").type(MediaType.APPLICATION_JSON)
                .entity("{\"handle\":\"fdsa\", \"tenant\":\"test\"}").put(ClientResponse.class);

        Assert.assertEquals(Status.OK, cr.getClientResponseStatus());
    }

}
