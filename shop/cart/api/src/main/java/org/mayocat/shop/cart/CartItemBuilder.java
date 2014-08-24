/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart;

import org.mayocat.shop.taxes.PriceWithTaxes;
import org.mayocat.shop.taxes.Taxable;

/**
 * @version $Id$
 */
public class CartItemBuilder
{
    private Taxable item;

    private Long quantity;

    private PriceWithTaxes unitPrice;

    public CartItemBuilder item(Taxable item)
    {
        this.item = item;
        return this;
    }

    public CartItemBuilder quantity(Long quantity)
    {
        this.quantity = quantity;
        return this;
    }

    public CartItemBuilder unitPrice(PriceWithTaxes unitPrice)
    {
        this.unitPrice = unitPrice;
        return this;
    }

    public CartItem build() {
        return new CartItem(
                item,
                quantity,
                unitPrice
        );
    }
}
