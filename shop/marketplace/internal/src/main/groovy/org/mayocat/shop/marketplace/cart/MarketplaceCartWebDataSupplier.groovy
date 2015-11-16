/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.cart

import groovy.transform.CompileStatic
import org.mayocat.context.WebContext
import org.mayocat.shop.cart.Cart
import org.mayocat.shop.cart.CartManager
import org.mayocat.shop.front.WebDataSupplier
import org.mayocat.shop.marketplace.web.delegate.WithMarketplaceCartWebObjectBuilder
import org.xwiki.component.annotation.Component

import javax.inject.Inject

/**
 * @version $Id$
 */
@Component("marketplaceCart")
@CompileStatic
class MarketplaceCartWebDataSupplier implements WebDataSupplier, WithMarketplaceCartWebObjectBuilder
{
    @Inject
    CartManager cartManager

    @Inject
    WebContext context

    @Override
    void supply(Map<String, Object> data)
    {
        if (context.tenant) {
            return
        }

        if (data.containsKey("cart")) {
            // If the cart data has already been supplied (because for example this is a cart request), we pass
            return
        }

        Cart cart = cartManager.cart
        data.put("cart", this.buildCartWebObject(cart))
    }
}
