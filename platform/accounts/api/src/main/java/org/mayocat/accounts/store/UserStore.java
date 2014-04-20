/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.store;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.mayocat.accounts.model.User;
import org.mayocat.accounts.model.Role;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityStore;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.Store;

@org.xwiki.component.annotation.Role
public interface UserStore extends Store<User, UUID>, EntityStore
{
    User create(@Valid User user, Role initialRole) throws EntityAlreadyExistsException, InvalidEntityException;

    User findUserByEmailOrUserName(String userNameOrEmail);

    List<Role> findRolesForUser(User user);
}
