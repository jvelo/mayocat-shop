package org.mayocat.shop.cart.model;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * @version $Id$
 */
public class Cart
{

    private Set<CartItem> items = Sets.newHashSet();

    public Cart()
    {
    }

    public void addItem(CartItem item)
    {
        items.add(item);
    }
}
