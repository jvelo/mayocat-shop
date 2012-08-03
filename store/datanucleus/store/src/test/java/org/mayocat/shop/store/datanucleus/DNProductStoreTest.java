package org.mayocat.shop.store.datanucleus;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.rules.ExpectedException;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.ProductStore;
import org.mayocat.shop.store.StoreException;
import org.xwiki.test.annotation.MockingRequirement;

/**
 * Unit tests for the product store. Note: This tests is really about datanucleus persistance, so bean-validation
 * constraints are not tested here. They are tested both in the model module directly and in full-stack REST
 * integrations test.
 */
public class DNProductStoreTest extends AbstractStoreEntityTestCase
{
    @MockingRequirement(exceptions = PersistenceManagerProvider.class)
    private DNProductStore ps;

    @MockingRequirement(exceptions = PersistenceManagerProvider.class)
    private DNTenantStore ts;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Tenant tenant;

    @Before
    public void registerTenant() throws StoreException
    {
        this.tenant = this.ts.findByHandle("default");
        if (this.tenant == null) {
            this.ts.create(new Tenant("default"));
            this.tenant = this.ts.findByHandle("default");
        }
    }

    @Test
    public void testCreateProduct() throws StoreException
    {
        Product p = new Product();
        p.setHandle("My-Handle");

        ps.create(p);

        Product p2 = ps.findByHandle("My-Handle");
        Assert.assertNotNull(p2);
    }

    @Test
    public void testCreateProductWithSameHandleButDifferentTenant() throws Exception
    {
        Product p = new Product();
        p.setHandle("My-Handle");

        ps.create(p);
        
        this.setTenantToResolveTo("other");
        this.setUpPersistenceManager();
        
        this.ps = this.getComponentManager().getInstance(ProductStore.class, "datanucleus");
        
        Product p2 = new Product();
        p2.setHandle("My-Handle");;

        ps.create(p2);

        // No exception thrown -> OK
    }

    @Test
    public void testCreateProductThatAlreadyExistsForTenant() throws StoreException
    {
        thrown.expect(StoreException.class);
        thrown.expectMessage(JUnitMatchers
            .containsString("integrity constraint violation"));

        Product p = new Product();
        p.setHandle("My-Handle");

        ps.create(p);

        Product p2 = new Product();
        p2.setHandle("My-Handle");

        ps.create(p2);
    }

    @Test
    public void testUpdateProduct() throws StoreException
    {
        Product p = new Product();
        p.setHandle("My-Handle");

        ps.create(p);
        ps.update(p);

        assertNotNull(ps.findByHandle("My-Handle"));
    }
}
