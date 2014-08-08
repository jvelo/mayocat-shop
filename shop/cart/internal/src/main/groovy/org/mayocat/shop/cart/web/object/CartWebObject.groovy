/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.web.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.common.collect.Lists
import groovy.transform.CompileStatic
import org.mayocat.image.model.Image
import org.mayocat.shop.cart.Cart
import org.mayocat.shop.cart.CartItem
import org.mayocat.shop.catalog.model.Purchasable
import org.mayocat.shop.catalog.web.object.PriceWebObject
import org.mayocat.shop.shipping.ShippingOption
import org.mayocat.shop.shipping.ShippingService

/**
 * @version $Id$
 */
@CompileStatic
class CartWebObject
{
    Long numberOfItems = 0l

    List<CartItemWebObject> items = Lists.newArrayList()

    PriceWebObject itemsTotal

    PriceWebObject itemsTotalExclusiveOfTaxes

    PriceWebObject total

    PriceWebObject totalExclusiveOfTaxes

    boolean hasShipping

    PriceWebObject shipping

    List<ShippingOptionWebObject> shippingOptions

    ShippingOptionWebObject selectedShippingOption

    // See https://github.com/FasterXML/jackson-core/issues/79
    @JsonIgnore
    public Boolean isHasShipping()
    {
        hasShipping
    }

    def withCart(ShippingService shippingService, Cart cart, Locale locale, List<Image> images)
    {
        items = [] as List<CartItemWebObject>

        // Total
        total = new PriceWebObject()
        total.withPrice(cart.total().incl(), cart.currency(), locale)

        totalExclusiveOfTaxes = new PriceWebObject()
        totalExclusiveOfTaxes.withPrice(cart.total().excl(), cart.currency(), locale)

        itemsTotalExclusiveOfTaxes = new PriceWebObject()
        itemsTotalExclusiveOfTaxes.withPrice(cart.total().excl(), cart.currency(), locale)

        // Items total
        itemsTotal = new PriceWebObject()
        itemsTotal.withPrice(cart.itemsTotal().incl(), cart.currency(), locale)

        itemsTotalExclusiveOfTaxes = new PriceWebObject()
        itemsTotalExclusiveOfTaxes.withPrice(cart.itemsTotal().excl(), cart.currency(), locale)

        cart.items().each({ CartItem cartItem ->
            CartItemWebObject cartItemWebObject = new CartItemWebObject()
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

            cartItemWebObject.withUnitPrice(cartItem.unitPrice().incl(), cart.currency(), locale)
            cartItemWebObject.
                    withUnitPriceExclusiveOfTaxes(cartItem.unitPrice().excl(), cart.currency(), locale)

            cartItemWebObject.withItemTotal(cartItem.total().incl(), cart.currency(), locale)
            cartItemWebObject.withItemTotalExclusiveOfTaxes(cartItem.total().excl(), cart.currency(), locale)

            items << cartItemWebObject
            numberOfItems += quantity
        })

        hasShipping = shippingService.isShippingEnabled()

        if (hasShipping) {
            if (cart.selectedShippingOption().isPresent()) {
                shipping = new PriceWebObject()
                shipping.withPrice(cart.selectedShippingOption().get().getPrice(), cart.currency(), locale)

                selectedShippingOption = new ShippingOptionWebObject()
                selectedShippingOption.withOption(shippingService, cart.selectedShippingOption().get(),
                        cart.currency(), locale)
                selectedShippingOption.selected = true
            }

            /*
            shippingOptions = [] as List<ShippingOptionWebObject>;
            List<ShippingOption> availableOptions = shippingService.getOptions(cart.getItems());
            for (ShippingOption option : availableOptions) {
                ShippingOptionWebObject shippingOptionWebObject = new ShippingOptionWebObject()
                shippingOptionWebObject.withOption(shippingService, option, cart.currency(), locale)

                if (cart.selectedShippingOption.carrierId == option.carrierId) {
                    shippingOptionWebObject.selected = true
                }

                shippingOptions << shippingOptionWebObject
            }
            */
        }
    }
}
