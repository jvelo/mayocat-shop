package org.mayocat.shop.store.datanucleus;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.rules.ExpectedException;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.StoreException;
import org.xwiki.test.AbstractMockingComponentTestCase;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.annotation.MockingRequirement;

/**
 * Unit tests for the product store. Note: This tests is really about datanucleus persistance, so bean-validation
 * constraints are not tested here. They are tested both in the model module directly and in full-stack REST
 * integrations test.
 */
@ComponentList(HsqldbTestingPersistanceManagerFactoryProvider.class)
public class DNProductStoreTest extends AbstractMockingComponentTestCase
{
    @MockingRequirement(exceptions = PersistanceManagerFactoryProdiver.class)
    private DNProductStore ps;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreateProduct() throws StoreException
    {
        Tenant t = new Tenant("mytenant");

        Product p = new Product();
        p.setHandle("My-Handle");
        p.setTenant(t);

        ps.create(p);

        Product p2 = ps.findByTenantAndHandle(t, "My-Handle");
        Assert.assertNotNull(p2);
    }

    @Test
    public void testCreateProductWithSameHandleButDifferentTenant() throws StoreException
    {
        Tenant t = new Tenant("my-tenant");
        Product p = new Product();
        p.setHandle("My-Handle");
        p.setTenant(t);

        ps.create(p);

        Tenant t2 = new Tenant("my-other-tenant");
        Product p2 = new Product();
        p2.setHandle("My-Handle");
        p2.setTenant(t2);

        ps.create(p2);

        // No exception thrown -> OK
    }

    @Test
    public void testCreateProductThatAlreadyExistsForTenant() throws StoreException
    {
        thrown.expect(StoreException.class);
        thrown.expectMessage(JUnitMatchers
            .containsString("Product with handle [My-Handle] already exists for tenant [my-tenant]"));

        Tenant t = new Tenant("my-tenant");

        Product p = new Product();
        p.setHandle("My-Handle");
        p.setTenant(t);

        ps.create(p);

        Product p2 = new Product();
        p2.setHandle("My-Handle");
        p2.setTenant(t);

        ps.create(p2);
    }

    @Test
    public void testUpdateProduct() throws StoreException
    {
        Tenant t = new Tenant("my-tenant");
        Product p = new Product();
        p.setHandle("My-Handle");
        p.setTenant(t);

        ps.create(p);
        ps.update(p);

        assertNotNull(ps.findByTenantAndHandle(t, "My-Handle"));
    }
}
