/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.store.memory;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.accounts.model.Role;
import org.mayocat.accounts.model.User;
import org.mayocat.accounts.store.UserStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.InvalidEntityException;

/**
 * Tests for {@link MemoryUserStore}.
 *
 * @version $Id$
 */
public class MemoryUserStoreTest
{
    private UserStore userStore;

    @Before
    public void setUpStore()
    {
        userStore = new MemoryUserStore();
    }

    @Test
    public void testCreateUserAndRetrieveUserRole() throws InvalidEntityException, EntityAlreadyExistsException
    {
        User user = userStore.create(new User());
        Assert.assertEquals(Arrays.asList(Role.ADMIN), userStore.findRolesForUser(user));

        User other = userStore.create(new User(), Role.GOD);
        Assert.assertEquals(Arrays.asList(Role.GOD), userStore.findRolesForUser(other));
    }
    @Test
    public void testFindByEmailOrUserName() throws InvalidEntityException, EntityAlreadyExistsException
    {
        User user = new User();
        user.setEmail("john@doe.com");
        user.setSlug("jdoe");

        User other = new User();
        other.setEmail("jane@doe.com");
        other.setSlug("jane");

        user = userStore.create(user);
        other = userStore.create(other);

        Assert.assertEquals(user, userStore.findUserByEmailOrUserName("jdoe"));
        Assert.assertEquals(user, userStore.findUserByEmailOrUserName("john@doe.com"));
        Assert.assertNull(userStore.findUserByEmailOrUserName("stan@smith.com"));

    }

}
