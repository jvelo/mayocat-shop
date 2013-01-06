package org.mayocat.shop.store;

import java.util.List;

import javax.validation.Valid;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.User;
import org.mayocat.shop.model.Role;

@org.xwiki.component.annotation.Role
public interface UserStore extends Store<User, Long>
{
    void create(@Valid User user, Tenant tenant, Role initialRole) throws EntityAlreadyExistsException, InvalidEntityException,
        StoreException;

    void update(@Valid User user, Tenant tenant) throws EntityDoesNotExistsException, InvalidEntityException,
        StoreException;

    User findById(Long id) throws StoreException;

    User findByEmailOrUserNameAndTenant(String userNameOrEmail, Tenant tenant) throws StoreException;

    List<User> findAll(Tenant tenant, Integer number, Integer offset) throws StoreException;

    List<Role> findRolesForUser(User user) throws StoreException;
    
}
