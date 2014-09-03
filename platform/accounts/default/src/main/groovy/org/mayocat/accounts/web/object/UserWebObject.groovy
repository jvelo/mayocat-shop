/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.web.object

import groovy.transform.CompileStatic
import org.mayocat.accounts.model.User

/**
 * @version $Id$
 */
@CompileStatic
class UserWebObject
{
    String email

    String username

    Boolean active

    UserWebObject withUser(User user)
    {
        email = user.email
        username = user.slug
        active = user.active

        this
    }
}
