/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.authorization;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.accounts.model.Role;
import org.mayocat.accounts.model.User;
import org.mayocat.accounts.store.UserStore;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

@Component
public class DefaultGatekeeper implements Gatekeeper
{
    @Inject
    private Provider<UserStore> userStore;

    @Inject
    private Logger logger;

    @Override
    public boolean userHasRole(User user, Role role)
    {
        List<Role> roles = userStore.get().findRolesForUser(user);
        for (Role userRole : roles) {
            if (role.equals(userRole)) {
                return true;
            }
        }
        return false;
    }
}
