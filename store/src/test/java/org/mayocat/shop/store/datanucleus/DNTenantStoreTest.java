package org.mayocat.shop.store.datanucleus;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.StoreException;
import org.xwiki.test.AbstractMockingComponentTestCase;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.annotation.MockingRequirement;

/**
 * Unit tests for the tenant store.
 * 
 * Note: This tests is really about datanucleus persistance, so bean-validation constraints are not tested here.
 * They are tested both in the model module directly and in full-stack REST integrations test.
 */
@ComponentList(HsqldbTestingPersistanceManagerFactoryProvider.class)
public class DNTenantStoreTest extends AbstractMockingComponentTestCase
{

    @MockingRequirement(exceptions=PersistanceManagerFactoryProdiver.class)
    private DNTenantStore tenantStore;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testCreateTenant() throws StoreException
    {
        Tenant t = new Tenant("mytenant");
        
        tenantStore.create(t);
        Tenant t2 = tenantStore.findByHandle("mytenant");
        
        Assert.assertEquals(t, t2);
    }
    
    @Test
    public void testCreateTenantFailsWhenTenantWithSameHandleAlreadyExists() throws StoreException
    {   
        thrown.expect(StoreException.class);
        //thrown.expectMessage(JUnitMatchers.containsString("integrity constraint violation: unique constraint"));
        
        Tenant t = new Tenant("mytenant");
        tenantStore.create(t);
        
        Tenant t2 = new Tenant("mytenant");
        tenantStore.create(t2);
    }
}
