/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.mayocat.shop.shipping.ShippingOption;
import org.mayocat.shop.taxes.PriceWithTaxes;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class CartBuilder
{
    private Currency currency;

    private List<CartItem> items = new ArrayList<>();

    private PriceWithTaxes itemsTotal;

    private Optional<ShippingOption> selectedShippingOption = Optional.absent();

    private Optional<PriceWithTaxes> shipping = Optional.absent();

    public CartBuilder currency(Currency currency)
    {
        this.currency = currency;
        return this;
    }

    public CartBuilder addItem(CartItem item)
    {
        this.items.add(item);
        return this;
    }

    public CartBuilder itemsTotal(PriceWithTaxes itemsTotal)
    {
        this.itemsTotal = itemsTotal;
        return this;
    }

    public CartBuilder selectedShippingOption(ShippingOption selectedShippingOption)
    {
        this.selectedShippingOption = Optional.fromNullable(selectedShippingOption);
        return this;
    }

    public CartBuilder selectedShippingOption(Optional<ShippingOption> selectedShippingOption)
    {
        this.selectedShippingOption = selectedShippingOption;
        return this;
    }

    public CartBuilder setShipping(PriceWithTaxes shipping)
    {
        this.shipping = Optional.fromNullable(shipping);
        return this;
    }

    public Cart build()
    {
        return new Cart(
                currency,
                items,
                itemsTotal,
                selectedShippingOption,
                shipping
        );
    }
}
