/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.ShippingOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class Cart implements Serializable
{
    private static final transient Logger LOGGER = (Logger) LoggerFactory.getLogger(Cart.class);

    private Map<Purchasable, Long> items = Maps.newLinkedHashMap();

    private Currency currency;

    private ShippingOption selectedShippingOption;

    public Cart(Currency currency)
    {
        this.currency = currency;
    }

    public void addItem(Purchasable item)
    {
        addItem(item, 1l);
    }

    public void removeItem(Purchasable item)
    {
        items.remove(item);
    }

    public void setItem(Purchasable item, Long quantity)
    {
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(quantity);
        Preconditions.checkNotNull(item.getId());

        Preconditions.checkNotNull(quantity);
        Preconditions.checkArgument(quantity > 0);

        BigDecimal unitPrice = null;
        if (item.getUnitPrice() != null && !item.getParent().isPresent()) {
            unitPrice = item.getUnitPrice();
        } else if (item.getParent().isPresent() && item.getParent().get().isLoaded()) {
            unitPrice = item.getParent().get().get().getUnitPrice();
        }

        Preconditions.checkArgument(unitPrice.compareTo(BigDecimal.ZERO) > 0);

        items.put(item, quantity);
    }

    public void addItem(Purchasable item, Long quantity)
    {
        LOGGER.debug("Adding item {} ({}) to cart", item, quantity);

        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(quantity);
        Preconditions.checkNotNull(item.getId());

        Preconditions.checkNotNull(quantity);
        Preconditions.checkArgument(quantity > 0);

        BigDecimal unitPrice = null;
        if (item.getUnitPrice() != null && !item.getParent().isPresent()) {
            unitPrice = item.getUnitPrice();
        } else if (item.getParent().isPresent() && item.getParent().get().isLoaded()) {
            unitPrice = item.getParent().get().get().getUnitPrice();
        }

        Preconditions.checkNotNull(unitPrice);
        Preconditions.checkArgument(unitPrice.compareTo(BigDecimal.ZERO) > 0);

        if (items.containsKey(item)) {
            Long newQuantity = items.get(item) + quantity;
            items.put(item, newQuantity);
        } else {
            items.put(item, quantity);
        }

        LOGGER.debug("Cart now contains {} items", this.items.size());
    }

    public Map<Purchasable, Long> getItems()
    {
        return items;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public BigDecimal getItemTotal(Purchasable item)
    {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal unitPrice = item.getUnitPrice();
        if (unitPrice == null && item.getParent().isPresent() && item.getParent().get().isLoaded()) {
            unitPrice = item.getParent().get().get().getUnitPrice();
        }
        if (items.containsKey(item) && unitPrice != null && items.get(item) > 0) {
            total = total.add(unitPrice.multiply(BigDecimal.valueOf(items.get(item))));
        }
        return total;
    }

    public BigDecimal getItemsTotal()
    {
        BigDecimal total = BigDecimal.ZERO;
        for (Purchasable item : items.keySet()) {
            total = total.add(getItemTotal(item));
        }
        return total;
    }

    public BigDecimal getTotal()
    {
        BigDecimal total = getItemsTotal();
        if (getSelectedShippingOption() != null) {
            total = total.add(getSelectedShippingOption().getPrice());
        }
        return total;
    }

    public ShippingOption getSelectedShippingOption()
    {
        return selectedShippingOption;
    }

    public void setSelectedShippingOption(ShippingOption selectedOption)
    {
        this.selectedShippingOption = selectedOption;
    }

    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    public void empty()
    {
        items.clear();
        this.selectedShippingOption = null;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cart other = (Cart) obj;

        return Objects.equal(this.items, other.items)
                && Objects.equal(this.currency, other.currency)
                && Objects.equal(this.selectedShippingOption, other.selectedShippingOption);
    }
}
