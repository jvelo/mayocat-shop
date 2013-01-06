package org.mayocat.shop.service;

import org.mayocat.shop.model.User;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Role;

@Role
public interface UserService extends EntityRepositoryService<User>
{
    User findByEmailOrUserName(String userNameOrEmail) throws StoreException;
    
    boolean hasUsers() throws StoreException;
    
    void createInitialUser(User user) throws StoreException;
    
    void create(User user) throws StoreException, EntityAlreadyExistsException, InvalidEntityException;
}
