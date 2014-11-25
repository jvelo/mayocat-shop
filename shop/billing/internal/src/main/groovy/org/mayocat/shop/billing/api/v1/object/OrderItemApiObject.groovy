/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.api.v1.object

import com.google.common.collect.Maps
import groovy.transform.CompileStatic
import org.mayocat.shop.billing.model.OrderItem

/**
 * @version $Id$
 */
@CompileStatic
class OrderItemApiObject
{
    String type;

    String title;

    Long quantity;

    BigDecimal unitPrice;

    BigDecimal itemTotal;

    BigDecimal vatRate;

    Map<String, Object> data = Maps.newHashMap();

    OrderItemApiObject withOrderItem(OrderItem orderItem)
    {
        this.type = orderItem.type
        this.title = orderItem.title
        this.quantity = orderItem.quantity
        this.unitPrice = orderItem.unitPrice
        this.itemTotal = orderItem.itemTotal
        this.vatRate = orderItem.vatRate
        this.data = orderItem.data

        this
    }
}
