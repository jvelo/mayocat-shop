package org.mayocat.shop.store.datanucleus;

import org.junit.Test;
import org.xwiki.test.annotation.MockingRequirement;

public class DNUserRoleStoreTest extends AbstractStoreEntityTestCase
{
    @MockingRequirement(exceptions=PersistenceManagerProvider.class)
    private DNUserRoleStore userRoleStore;

    @Test
    public void testCreateUserRole()
    {
        
    }
    
}
