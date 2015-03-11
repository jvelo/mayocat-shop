/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart;

import java.math.BigDecimal;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.taxes.PriceWithTaxes;

import com.google.common.base.Preconditions;

/**
 * An immutable cart item, with its associated prices : unit price and total price, with applied taxes information.
 *
 * @version $Id$
 */
public class CartItem
{
    private final Tenant tenant;

    private final Purchasable item;

    private final Long quantity;

    private final PriceWithTaxes unitPrice;

    private final PriceWithTaxes total;

    public CartItem(Tenant tenant, Purchasable item, Long quantity, PriceWithTaxes unitPrice)
    {
        Preconditions.checkNotNull(tenant);
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(quantity);
        Preconditions.checkNotNull(unitPrice);

        PriceWithTaxes total = unitPrice.multiply(quantity);

        this.tenant = tenant;
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = total;
    }

    public Tenant tenant()
    {
        return tenant;
    }

    public Purchasable item()
    {
        return item;
    }

    public Long quantity()
    {
        return quantity;
    }

    public PriceWithTaxes unitPrice()
    {
        return unitPrice;
    }

    public PriceWithTaxes total()
    {
        return total;
    }
}
