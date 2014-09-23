/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web.object

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.accounts.model.Tenant
import org.mayocat.configuration.PlatformSettings
import org.mayocat.image.model.Image
import org.mayocat.shop.cart.Cart
import org.mayocat.shop.cart.CartItem
import org.mayocat.shop.cart.web.object.AbstractCartWebObject
import org.mayocat.shop.cart.web.object.CartItemWebObject
import org.mayocat.shop.catalog.model.Purchasable
import org.mayocat.theme.ThemeDefinition

/**
 * @version $Id$
 */
@CompileStatic
class MarketplaceCartWebObject extends AbstractCartWebObject
{
    List<MarketplaceCartItemWebObject> items

    @Override
    void addCartItems(Cart cart, Locale locale, List<Image> images, PlatformSettings platformSettings,
            Optional<ThemeDefinition> themeDefinition)
    {
        items = [] as List<MarketplaceCartItemWebObject>

        cart.items().each({ CartItem cartItem ->
            MarketplaceCartItemWebObject cartItemWebObject = new MarketplaceCartItemWebObject()
            Long quantity = cartItem.quantity()
            Purchasable purchasable = cartItem.item()
            Purchasable product

            if (purchasable.parent.isPresent()) {
                if (!purchasable.parent.get().isLoaded()) {
                    // This should never happen
                    throw new RuntimeException("Can't build cart with a variant which parent product is not loaded")
                }
                product = purchasable.parent.get().get()
                cartItemWebObject.variant = purchasable.title
            } else {
                product = purchasable
            }

            cartItemWebObject.withPurchasable(product, quantity)
            cartItemWebObject.withUnitPrice(cartItem.unitPrice(), cart.currency(), locale)
            cartItemWebObject.withItemTotal(cartItem.total(), cart.currency(), locale)

            Image featuredImage = images.find({ Image image ->
                image.attachment.parentId == cartItem.item().id
            })
            if (featuredImage) {
                cartItemWebObject.withFeaturedImage(cartItem.tenant(), featuredImage, platformSettings)
            }

            items << cartItemWebObject
            numberOfItems += quantity
        })
    }
}
