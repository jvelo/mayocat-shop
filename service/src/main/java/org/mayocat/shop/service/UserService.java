package org.mayocat.shop.service;

import org.mayocat.shop.model.User;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Role;

@Role
public interface UserService extends EntityWithSlugRepositoryService<User>
{
    User findByEmailOrUserName(String userNameOrEmail) throws StoreException;
    
    boolean hasUsers() throws StoreException;
    
    void createInitialUser(User user) throws StoreException;
}
