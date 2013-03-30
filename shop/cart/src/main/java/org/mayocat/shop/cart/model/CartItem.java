package org.mayocat.shop.cart.model;

import java.math.BigDecimal;

import org.mayocat.shop.catalog.model.Purchasable;

import com.google.common.base.Preconditions;

/**
 * @version $Id$
 */
public class CartItem
{
    private Purchasable purchasable;

    private Integer quantity;

    public CartItem(Purchasable purchasable, Integer quantity)
    {
        Preconditions.checkNotNull(purchasable);
        Preconditions.checkNotNull(quantity);
        Preconditions.checkNotNull(purchasable.getId());

        Preconditions.checkNotNull(quantity);
        Preconditions.checkArgument(quantity > 0);

        this.purchasable = purchasable;
        this.quantity = quantity;
    }

    public Purchasable getPurchasable()
    {
        return purchasable;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public void addOne()
    {
        this.quantity += 1;
    }
}
