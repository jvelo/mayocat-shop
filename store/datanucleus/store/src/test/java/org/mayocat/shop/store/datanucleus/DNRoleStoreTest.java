package org.mayocat.shop.store.datanucleus;

import org.junit.Test;
import org.mayocat.shop.model.Role;
import org.mayocat.shop.authorization.Capability;
import org.xwiki.test.annotation.MockingRequirement;

public class DNRoleStoreTest extends AbstractStoreEntityTestCase
{
    @MockingRequirement(exceptions = PersistenceManagerProvider.class)
    private DNRoleStore roleStore;

    @Test
    public void testCreateRole() throws Exception
    {
        Role role = new Role();
        role.setName(Role.RoleName.ADMIN);
        role.addToCapabilities(new Capability("ALL"));
        roleStore.create(role);
    }
}
