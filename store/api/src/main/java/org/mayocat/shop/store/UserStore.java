package org.mayocat.shop.store;

import org.mayocat.shop.model.User;
import org.xwiki.component.annotation.Role;

@Role
public interface UserStore
{
    void persist(User user) throws StoreException;
    
    User findById(Long id) throws StoreException;
    
    User findByEmailOrUserName(String userNameOrEmail) throws StoreException;
}
