package org.mayocat.shop.cart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
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
    private static final long serialVersionUID = 4776955705209536037L;

    private Map<Purchasable, Long> items = Maps.newLinkedHashMap();

    private Currency currency;

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
        Preconditions.checkArgument(item.getUnitPrice().compareTo(BigDecimal.ZERO) > 0);

        items.put(item, quantity);
    }

    public void addItem(Purchasable item, Long quantity)
    {
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(quantity);
        Preconditions.checkNotNull(item.getId());

        Preconditions.checkNotNull(quantity);
        Preconditions.checkArgument(quantity > 0);
        Preconditions.checkArgument(item.getUnitPrice().compareTo(BigDecimal.ZERO) > 0);

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

    public BigDecimal getItemTotal(Purchasable item)
    {
        BigDecimal total = BigDecimal.ZERO;
        if (items.containsKey(item) && item.getUnitPrice() != null && items.get(item) > 0) {
            total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(items.get(item))));
        }
        return total;
    }

    public BigDecimal getTotal()
    {
        BigDecimal total = BigDecimal.ZERO;
        for (Purchasable item : items.keySet()) {
            total = total.add(getItemTotal(item));
        }
        return total;
    }

    public void empty()
    {
        items.clear();
    }
}
