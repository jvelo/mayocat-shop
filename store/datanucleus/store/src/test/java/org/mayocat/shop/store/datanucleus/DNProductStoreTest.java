package org.mayocat.shop.store.datanucleus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.xwiki.observation.ObservationManager;
import org.xwiki.test.annotation.MockingRequirement;

/**
 * Unit tests for the product store. Note: This tests is really about datanucleus persistence, so bean-validation
 * constraints are not tested here. They are tested both in the model module directly and in full-stack REST
 * integrations test.
 */
public class DNProductStoreTest extends AbstractStoreEntityTestCase
{
    @MockingRequirement(exceptions = {PersistenceManagerProvider.class, ObservationManager.class})
    private DNProductStore ps;

    @MockingRequirement(exceptions = {PersistenceManagerProvider.class})
    private DNTenantStore ts;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Tenant tenant;

    @Before
    public void registerTenant() throws Exception
    {
        this.tenant = this.ts.findBySlug("default");
        if (this.tenant == null) {
            this.ts.create(new Tenant("default"));
            this.tenant = this.ts.findBySlug("default");
        }
    }

    @Test
    public void testCreateProduct() throws Exception
    {
        Product p = new Product();
        p.setSlug("waterproof-fly-swatter");
        p.setTitle("Waterproof Fly Swatter");

        ps.create(p);

        Product p2 = ps.findBySlug("waterproof-fly-swatter");
        Assert.assertNotNull(p2);
    }

    @Test
    public void testCreateProductWithSameHandleButDifferentTenant() throws Exception
    {
        Product p = new Product();
        p.setSlug("leopard-fishnet-tights");
        p.setTitle("Leopard Fishnet Tights");

        ps.create(p);
        
        this.setTenantToResolveTo("other");
        
        Product p2 = new Product();
        p2.setSlug("leopard-fishnet-tights");;
        p2.setTitle("Leopard Fishnet Tights");

        ps.create(p2);

        // No exception thrown -> OK
    }

    @Test
    public void testCreateProductThatAlreadyExistsForTenant() throws Exception
    {
        thrown.expect(EntityAlreadyExistsException.class);

        Product p = new Product();
        p.setSlug("peugeot-403-convertible");
        p.setTitle("Peugeot 403 convertible");

        ps.create(p);

        Product p2 = new Product();
        p2.setSlug("peugeot-403-convertible");
        p2.setTitle("Peugeot 403 convertible");
        
        ps.create(p2);
    }

    @Test
    public void testUpdateProduct() throws Exception
    {
        Product product = new Product();
        product.setSlug("face-skin-mask");
        product.setTitle("Face Skin Mask");
        
        ps.create(product);

        product = new Product();
        product.setSlug("face-skin-mask");
       
        product.setTitle("Horse Head Mask");
        ps.update(product);

        Product retrieved = ps.findBySlug("face-skin-mask"); 
        assertNotNull(retrieved);
        assertEquals("Horse Head Mask", retrieved.getTitle());
        assertEquals(product, retrieved);
    }
}
