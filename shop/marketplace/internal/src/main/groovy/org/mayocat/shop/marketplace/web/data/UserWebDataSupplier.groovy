/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web.data

import groovy.transform.CompileStatic
import org.mayocat.accounts.web.object.UserWebObject
import org.mayocat.context.WebContext
import org.mayocat.shop.front.WebDataSupplier
import org.xwiki.component.annotation.Component

import javax.inject.Inject

/**
 * @version $Id$
 */
@CompileStatic
@Component("userWebDataSupplier")
class UserWebDataSupplier implements WebDataSupplier
{
    @Inject
    WebContext context

    @Override
    void supply(Map<String, Object> data)
    {
        if (context.user) {
            data.put("user", new UserWebObject().withUser(context.user))
        }
    }
}
