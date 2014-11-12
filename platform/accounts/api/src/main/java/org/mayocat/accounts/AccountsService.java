/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.core.Response;

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

    void createInitialUser(@Valid User user)
            throws EntityAlreadyExistsException, InvalidEntityException, PasswordDoesNotMeetRequirementsException;

    void createUser(@Valid User user)
            throws EntityAlreadyExistsException, InvalidEntityException, PasswordDoesNotMeetRequirementsException;

    void createUser(@Valid User user, Map<String, Object> additionalContext)
            throws EntityAlreadyExistsException, InvalidEntityException, PasswordDoesNotMeetRequirementsException;

    List<Role> findRolesForUser(User user);

    User findUserByEmailOrUserName(String userNameOrEmail);

    User validateAccount(String validationKey) throws NoSuchValidationKeyException, IncompatibleConnectedUserException,
            UserAlreadyValidatedException;

    void changePassword(User user, String currentPassword, String newPassword) throws WrongPasswordException,
            PasswordDoesNotMeetRequirementsException;

    void createPasswordResetRequest(String emailOrUsername) throws UserNotFoundException;

    void resetPassword(String resetKey, String password)
            throws NoSuchPasswordResetKeyException, PasswordDoesNotMeetRequirementsException;
}
