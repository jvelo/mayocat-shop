package org.mayocat.shop.store;

import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.User;
import org.xwiki.component.annotation.Role;

@Role
public interface UserStore
{
    void persist(User user) throws StoreException;
    
    Product getUser(Long id) throws StoreException;
    
    Product getUserByEmailOrUserName(String userNameOrEmail) throws StoreException;
}
