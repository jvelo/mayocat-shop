package org.mayocat.shop.cart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.ShippingOption;
import org.mayocat.shop.shipping.model.Carrier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @version $Id$
 */
public class Cart implements Serializable
{
    private static final transient Logger LOGGER = (Logger) LoggerFactory.getLogger(Cart.class);

    private Map<Purchasable, Long> items = Maps.newLinkedHashMap();

    private Currency currency;

    private ShippingOption selectedOption;

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
        LOGGER.debug("Adding item {} ({}) to cart", item, quantity);

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
        if (items.containsKey(item) && item.getUnitPrice() != null && items.get(item) > 0) {
            total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(items.get(item))));
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
        if (getSelectedOption() != null) {
            total = total.add(getSelectedOption().getPrice());
        }
        return total;
    }

    public ShippingOption getSelectedOption()
    {
        return selectedOption;
    }

    public void setSelectedOption(ShippingOption selectedOption)
    {
        this.selectedOption = selectedOption;
    }

    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    public void empty()
    {
        items.clear();
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
                && Objects.equal(this.selectedOption, other.selectedOption);
    }
}
