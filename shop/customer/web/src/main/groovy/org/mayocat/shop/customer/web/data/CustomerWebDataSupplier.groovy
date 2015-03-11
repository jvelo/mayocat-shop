/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.web.data

import groovy.transform.CompileStatic
import org.mayocat.accounts.UserDataSupplier
import org.mayocat.context.WebContext
import org.mayocat.shop.front.WebDataSupplier
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Named

/**
 * @version $Id$
 */
@CompileStatic
@Component("customerWebDataSupplier")
class CustomerWebDataSupplier implements WebDataSupplier
{
    @Inject
    @Named("customer")
    UserDataSupplier dataSupplier

    @Inject
    WebContext context

    @Override
    void supply(Map<String, Object> data)
    {
        if (context.user) {
            dataSupplier.supply(context.user, data)
        }
    }
}
