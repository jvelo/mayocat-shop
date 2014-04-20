/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.front.context;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.catalog.front.representation.PriceRepresentation;
import org.mayocat.shop.catalog.model.Purchasable;

import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
public class CartContext
{
    private Long numberOfItems = 0l;

    private List<CartItemContext> items = Lists.newArrayList();

    private PriceRepresentation itemsTotal;

    private boolean hasShipping;

    private PriceRepresentation shipping;

    private List<ShippingOptionContext> shippingOptions;

    private ShippingOptionContext selectedShippingOption;

    private PriceRepresentation total;

    public CartContext(List<CartItemContext> items, Long numberOfItems, PriceRepresentation itemsTotal,
            PriceRepresentation total)
    {
        this.items = items;
        this.itemsTotal = itemsTotal;
        this.total = total;
        this.numberOfItems = numberOfItems;
    }

    public List<CartItemContext> getItems()
    {
        return items;
    }

    public PriceRepresentation getTotal()
    {
        return total;
    }

    public Long getNumberOfItems()
    {
        return numberOfItems;
    }

    public PriceRepresentation getShipping()
    {
        return shipping;
    }

    public void setShipping(PriceRepresentation shipping)
    {
        this.shipping = shipping;
    }

    public List<ShippingOptionContext> getShippingOptions()
    {
        return shippingOptions;
    }

    public void setShippingOptions(List<ShippingOptionContext> shippingOptions)
    {
        this.shippingOptions = shippingOptions;
    }

    public ShippingOptionContext getSelectedShippingOption()
    {
        return selectedShippingOption;
    }

    public void setSelectedShippingOption(ShippingOptionContext selectedShippingOption)
    {
        this.selectedShippingOption = selectedShippingOption;
    }

    public PriceRepresentation getItemsTotal()
    {
        return itemsTotal;
    }

    public boolean isHasShipping()
    {
        return hasShipping;
    }

    public void setHasShipping(boolean hasShipping)
    {
        this.hasShipping = hasShipping;
    }
}
