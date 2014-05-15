/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.api.v1.object

import groovy.transform.CompileStatic
import org.mayocat.accounts.model.User
import org.mayocat.rest.api.object.BaseApiObject

/**
 * @version $Id$
 */
@CompileStatic
class UserApiObject extends BaseApiObject {

    String slug;

    String email;

    String password;

    def withUser(User user) {
        slug = user.slug
        email = user.email
    }

    User toUser() {
        User user = new User();
        user.slug = slug
        user.email = email
        user.password = password
        user
    }
}
