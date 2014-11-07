/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.web.data

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.configuration.PlatformSettings
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.image.model.Image
import org.mayocat.shop.cart.Cart
import org.mayocat.shop.cart.CartManager
import org.mayocat.shop.cart.web.object.CartWebObject
import org.mayocat.shop.front.WebDataSupplier
import org.mayocat.shop.shipping.ShippingService
import org.xwiki.component.annotation.Component

import javax.inject.Inject

/**
 * @version $Id: ec51fcf363dcdfb8e1ced5c36d180fa88fee354e $
 */
@Component("cart")
@CompileStatic
class CartWebDataSupplier implements WebDataSupplier
{
    @Inject
    CartManager cartManager

    @Inject
    ShippingService shippingService

    @Inject
    GeneralSettings generalSettings

    @Inject
    PlatformSettings platformSettings

    @Inject
    WebContext context

    @Override
    public void supply(Map<String, Object> data)
    {
        if (context.tenant == null) {
            return;
        }

        if (data.containsKey("cart")) {
            // If the cart data has already been supplied (because for example this is a cart request), we pass
            return
        }

        Cart cart = cartManager.cart
        final Locale locale = generalSettings.locales.mainLocale.value

        CartWebObject cartWebObject = new CartWebObject()
        cartWebObject.withCart(shippingService, cart, locale, [] as List<Image>, platformSettings,
                Optional.fromNullable(context.theme?.definition))

        data.put("cart", cartWebObject)
    }
}
