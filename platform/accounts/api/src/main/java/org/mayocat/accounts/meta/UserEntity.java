/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.meta;

import org.mayocat.accounts.model.User;
import org.mayocat.meta.EntityMeta;

/**
 * @version $Id$
 */
public class UserEntity implements EntityMeta
{
    public static final String ID = "user";

    public static final String PATH = "users";

    @Override
    public String getEntityName()
    {
        return ID;
    }

    @Override
    public Class getEntityClass()
    {
        return User.class;
    }
}
