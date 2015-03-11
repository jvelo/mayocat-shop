/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.web.object

import groovy.transform.CompileStatic
import org.mayocat.shop.billing.model.Order

/**
 * @version $Id$
 */
@CompileStatic
class OrderWebObject extends OrderSummaryWebObject
{
    List<OrderItemWebObject> items = [] as List<OrderItemWebObject>

    @Override
    OrderWebObject withOrder(Order order, Locale locale)
    {
        super.withOrder(order, locale)
        this
    }
}
