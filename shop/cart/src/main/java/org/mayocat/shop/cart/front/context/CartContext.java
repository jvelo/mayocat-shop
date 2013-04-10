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

    private PriceRepresentation total;

    public CartContext(List<CartItemContext> items, Long numberOfItems, PriceRepresentation total)
    {
        this.items = items;
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
}
