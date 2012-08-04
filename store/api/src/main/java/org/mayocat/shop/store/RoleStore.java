package org.mayocat.shop.store;

import org.mayocat.shop.authorization.Capability;
import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.User;

public interface RoleStore
{
    void persist(Role user) throws StoreException;
    
    Role findById(Long id) throws StoreException;
    
    Role findByUserAndCapability(User user, Capability capability) throws StoreException;
}
