package org.mayocat.shop.cart.model;

import java.io.Serializable;
import java.util.Currency;
import java.util.Map;
import java.util.Set;

import org.mayocat.shop.catalog.model.Purchasable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @version $Id$
 */
public class Cart implements Serializable
{
    private Map<Purchasable, Long> items = Maps.newHashMap();

    private Currency currency;

    public Cart(Currency currency)
    {
        this.currency = currency;
    }

    public void addItem(Purchasable item)
    {
        addItem(item, 1l);
    }

    public void addItem(Purchasable item, Long quantity)
    {
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(quantity);
        Preconditions.checkNotNull(item.getId());

        Preconditions.checkNotNull(quantity);
        Preconditions.checkArgument(quantity > 0);

        if (items.containsKey(item)) {
            Long newQuantity = items.get(item) + quantity;
            items.put(item, newQuantity);
        } else {
            items.put(item, quantity);
        }
    }

    public Map<Purchasable, Long> getItems()
    {
        return items;
    }

    public Currency getCurrency()
    {
        return currency;
    }
}
