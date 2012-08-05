package org.mayocat.shop.store;

import java.util.List;

import org.mayocat.shop.model.User;
import org.xwiki.component.annotation.Role;

@Role
public interface UserStore extends Store<User>
{
    void create(User user) throws StoreException;
    
    User findById(Long id) throws StoreException;
    
    User findByEmailOrUserName(String userNameOrEmail) throws StoreException;
    
    List<User> findAll(int number, int offset) throws StoreException; 
}
