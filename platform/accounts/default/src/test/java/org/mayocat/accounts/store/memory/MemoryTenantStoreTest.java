package org.mayocat.accounts.store.memory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.store.TenantStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.InvalidEntityException;

/**
 * Tests for {@link MemoryTenantStore}.
 *
 * @version $Id$
 */
public class MemoryTenantStoreTest
{
    private TenantStore tenantStore;

    @Before
    public void setUpStore()
    {
        this.tenantStore = new MemoryTenantStore();
    }

    @Test
    public void testCreateAndFindByDefaultHost() throws InvalidEntityException, EntityAlreadyExistsException
    {
        Tenant tenant1 = new Tenant();
        tenant1.setDefaultHost("a-shop.com");

        tenant1 = tenantStore.create(tenant1);

        Tenant tenant2 = new Tenant();
        tenant2.setDefaultHost("my-shop.com");

        tenant2 = tenantStore.create(tenant2);

        Assert.assertEquals(tenant2, tenantStore.findByDefaultHost("my-shop.com"));
    }
}
