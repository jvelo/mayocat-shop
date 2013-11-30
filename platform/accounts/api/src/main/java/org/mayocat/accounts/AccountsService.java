/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts;

import java.util.List;

import javax.validation.Valid;

import org.mayocat.accounts.model.Role;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;

@org.xwiki.component.annotation.Role
public interface AccountsService
{
    // Tenant operations

    Tenant findTenant(String slug);

    Tenant findTenantByDefaultHost(String slug);

    Tenant createDefaultTenant() throws EntityAlreadyExistsException;

    void createTenant(@Valid Tenant tenant) throws EntityAlreadyExistsException, InvalidEntityException;

    void updateTenant(@Valid Tenant tenant) throws EntityDoesNotExistException, InvalidEntityException;

    List<Tenant> findAllTenants(Integer limit, Integer offset);

    Integer countAllTenants();

    // User operations

    boolean hasUsers();

    void createInitialUser(@Valid User user) throws EntityAlreadyExistsException, InvalidEntityException;

    void createUser(@Valid User user) throws EntityAlreadyExistsException, InvalidEntityException;

    List<Role> findRolesForUser(User user);

    public User findUserByEmailOrUserName(String userNameOrEmail);
}
