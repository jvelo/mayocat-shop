/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.web.object

import groovy.transform.CompileStatic
import org.mayocat.shop.catalog.web.object.PriceWebObject
import org.mayocat.shop.marketplace.web.object.MarketplaceProductWebObject

/**
 * @version $Id$
 */
@CompileStatic
class OrderItemWebObject
{
    MarketplaceProductWebObject product

    String title

    Long quantity

    PriceWebObject unitPrice

    PriceWebObject itemTotal
}
