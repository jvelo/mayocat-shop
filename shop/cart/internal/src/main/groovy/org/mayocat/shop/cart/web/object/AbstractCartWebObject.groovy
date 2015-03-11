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
import groovy.transform.CompileStatic
import org.mayocat.configuration.PlatformSettings
import org.mayocat.image.model.Image
import org.mayocat.shop.cart.Cart
import org.mayocat.shop.cart.CartItem
import org.mayocat.shop.catalog.model.Purchasable
import org.mayocat.shop.catalog.web.object.PriceWebObject
import org.mayocat.shop.shipping.ShippingOption
import org.mayocat.shop.shipping.ShippingService
import org.mayocat.theme.ThemeDefinition

/**
 * @version $Id$
 */
@CompileStatic
abstract class AbstractCartWebObject
{
    Long numberOfItems = 0l

    PriceWebObject itemsTotal

    PriceWebObject itemsTotalExclusiveOfTaxes

    CombinedTaxesWebObject itemsTaxes

    PriceWebObject total

    PriceWebObject totalExclusiveOfTaxes

    CombinedTaxesWebObject totalTaxes

    Boolean hasShipping

    PriceWebObject shipping

    List<ShippingOptionWebObject> shippingOptions

    ShippingOptionWebObject selectedShippingOption

    def withCart(ShippingService shippingService, Cart cart, Locale locale, List<Image> images = [] as List<Image>,
            PlatformSettings platformSettings, Optional<ThemeDefinition> themeDefinition = Optional.absent())
    {
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

        addCartItems(cart, locale, images, platformSettings, themeDefinition)

        hasShipping = shippingService.isShippingEnabled()

        if (hasShipping) {
            if (cart.selectedShippingOption().isPresent()) {
                shipping = new PriceWebObject()
                shipping.withPrice(cart.selectedShippingOption().get().getPrice().incl(), cart.currency(), locale)

                selectedShippingOption = new ShippingOptionWebObject()
                selectedShippingOption.withOption(shippingService, cart.selectedShippingOption().get(),
                        cart.currency(), locale)
                selectedShippingOption.selected = true
            }


            shippingOptions = [] as List<ShippingOptionWebObject>
            List<CartItem> items = cart.items()
            Map<Purchasable, Long> itemsAsMap = [:]
            items.each({ CartItem item ->
                itemsAsMap.put(item.item(), item.quantity())
            })
            List<ShippingOption> availableOptions = shippingService.getOptions(itemsAsMap);
            for (ShippingOption option : availableOptions) {
                ShippingOptionWebObject shippingOptionWebObject = new ShippingOptionWebObject()
                shippingOptionWebObject.withOption(shippingService, option, cart.currency(), locale)

                if (cart.selectedShippingOption().isPresent() &&
                        cart.selectedShippingOption().get().carrierId == option.carrierId)
                {
                    shippingOptionWebObject.selected = true
                }

                shippingOptions << shippingOptionWebObject
            }
        }
    }

    abstract void addCartItems(Cart cart, Locale locale, List<Image> images, PlatformSettings platformSettings,
            Optional<ThemeDefinition> themeDefinition);

}
