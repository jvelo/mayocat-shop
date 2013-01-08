package org.mayocat.shop.service;

import java.util.List;

import javax.validation.Valid;

import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.User;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.EntityDoesNotExistException;
import org.mayocat.shop.store.InvalidEntityException;

@org.xwiki.component.annotation.Role
public interface AccountsService
{
    // Tenant operations

    Tenant findTenant(String slug);

    void createTenant(@Valid Tenant tenant) throws EntityAlreadyExistsException, InvalidEntityException;

    void updateTenant(@Valid Tenant tenant) throws EntityDoesNotExistException, InvalidEntityException;

    // User operations

    boolean hasUsers();

    void createInitialUser(@Valid User user) throws EntityAlreadyExistsException, InvalidEntityException;

    void createUser(@Valid User user) throws EntityAlreadyExistsException, InvalidEntityException;

    List<Role> findRolesForUser(User user);

    public User findUserByEmailOrUserName(String userNameOrEmail);
}
