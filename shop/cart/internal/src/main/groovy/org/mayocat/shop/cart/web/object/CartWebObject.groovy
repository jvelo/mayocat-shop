/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.web.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.common.base.Optional
import com.google.common.collect.Lists
import groovy.transform.CompileStatic
import org.mayocat.image.model.Image
import org.mayocat.shop.cart.Cart
import org.mayocat.shop.cart.CartItem
import org.mayocat.shop.catalog.model.Purchasable
import org.mayocat.shop.catalog.web.object.PriceWebObject
import org.mayocat.shop.shipping.ShippingService
import org.mayocat.theme.ThemeDefinition

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

    CombinedTaxesWebObject itemsTaxes

    PriceWebObject total

    PriceWebObject totalExclusiveOfTaxes

    CombinedTaxesWebObject totalTaxes

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

    def withCart(ShippingService shippingService, Cart cart, Locale locale, List<Image> images = [] as List<Image>,
            Optional<ThemeDefinition> themeDefinition = Optional.absent())
    {
        items = [] as List<CartItemWebObject>

        // Items total
        itemsTotal = new PriceWebObject()
        itemsTotal.withPrice(cart.itemsTotal().incl(), cart.currency(), locale)

        itemsTotalExclusiveOfTaxes = new PriceWebObject()
        itemsTotalExclusiveOfTaxes.withPrice(cart.itemsTotal().excl(), cart.currency(), locale)

        if (!cart.itemsTotal().vat().equals(BigDecimal.ZERO)) {
            itemsTaxes = new CombinedTaxesWebObject([
                    vat: new PriceWebObject().withPrice(cart.itemsTotal().vat(), cart.currency(), locale)
            ])
        }

        // Total
        total = new PriceWebObject()
        total.withPrice(cart.total().incl(), cart.currency(), locale)

        totalExclusiveOfTaxes = new PriceWebObject()
        totalExclusiveOfTaxes.withPrice(cart.total().excl(), cart.currency(), locale)

        if (!cart.total().vat().equals(BigDecimal.ZERO)) {
            totalTaxes = new CombinedTaxesWebObject([
                    vat: new PriceWebObject().withPrice(cart.total().vat(), cart.currency(), locale)
            ])
        }

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
            cartItemWebObject.withUnitPrice(cartItem.unitPrice(), cart.currency(), locale)
            cartItemWebObject.withItemTotal(cartItem.total(), cart.currency(), locale)

            Image featuredImage = images.find({ Image image ->
                image.attachment.parentId == cartItem.item().id
            })
            if (featuredImage) {
                cartItemWebObject.withFeaturedImage(featuredImage, themeDefinition)
            }

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
