package org.mayocat.shop.store.datanucleus;

import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Shop;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.xwiki.test.annotation.MockingRequirement;

/**
 * Unit tests for the tenant store. Note: This tests is really about datanucleus persistence, so bean-validation
 * constraints are not tested here. They are tested both in the model module directly and in full-stack REST
 * integrations test.
 */
public class DNTenantStoreTest extends AbstractStoreEntityTestCase
{

    @MockingRequirement(exceptions = PersistenceManagerProvider.class)
    private DNTenantStore tenantStore;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreateTenant() throws Exception
    {
        Tenant t = new Tenant("mytenant");

        tenantStore.create(t);
        Tenant t2 = tenantStore.findBySlug("mytenant");

        Assert.assertEquals(t, t2);
    }

    @Test
    public void testCreateTenantFailsWhenTenantWithSameHandleAlreadyExists() throws Exception
    {
        thrown.expect(EntityAlreadyExistsException.class);

        Tenant t = new Tenant("mytenant");
        tenantStore.create(t);

        Tenant t2 = new Tenant("mytenant");
        tenantStore.create(t2);
    }

}
