/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.web.object

import groovy.transform.CompileStatic
import org.mayocat.rest.api.object.DateWebObject
import org.mayocat.shop.billing.model.Order
import org.mayocat.shop.catalog.web.object.PriceWebObject

/**
 * @version $Id$
 */
@CompileStatic
class OrderSummaryWebObject
{
    String slug

    String status

    PriceWebObject grandTotal

    PriceWebObject totalTaxes

    PriceWebObject itemsTotal

    PriceWebObject itemsTaxes

    PriceWebObject shipping

    PriceWebObject shippingTaxes

    PriceWebObject taxes

    DateWebObject date

    Integer numberOfItems

    String additionalInformation

    def withOrder(Order order, Locale locale)
    {
        this.slug = order.slug
        this.grandTotal = new PriceWebObject().withPrice(order.grandTotal, order.currency, locale)
        this.itemsTotal = new PriceWebObject().withPrice(order.itemsTotal, order.currency, locale)
        if (order.shipping != null) {
            this.shipping = new PriceWebObject().withPrice(order.shipping, order.currency, locale)
        }

        this.date = new DateWebObject().withDate(order.creationDate, locale)
        this.numberOfItems = order.numberOfItems
        this.status = order.status.toString().toLowerCase()
        this.additionalInformation = order.additionalInformation

        if (order.itemsTotal && order.itemsTotalExcl) {
            this.itemsTaxes = new PriceWebObject().withPrice(
                    (order.getShipping() ?: BigDecimal.ZERO) - (order.shippingExcl ?: BigDecimal.ZERO),
                    order.currency, locale)
        }

        if (order.grandTotal && order.grandTotalExcl) {
            this.totalTaxes = new PriceWebObject().withPrice(order.grandTotal.subtract(order.grandTotalExcl),
                    order.currency, locale)
        }

        if (order.shipping && order.shippingExcl) {
            this.shippingTaxes = new PriceWebObject().withPrice(order.shipping.subtract(order.shippingExcl),
                    order.currency, locale)
        }

        this
    }
}
