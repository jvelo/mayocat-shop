package org.mayocat.shop.store;

import org.mayocat.shop.authorization.Capability;
import org.mayocat.shop.model.User;

@org.xwiki.component.annotation.Role
public interface RoleStore extends Store<org.mayocat.shop.model.Role, Long>
{
    org.mayocat.shop.model.Role findById(Long id) throws StoreException;
    
    org.mayocat.shop.model.Role findByUserAndCapability(User user, Capability capability) throws StoreException;
}
