/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.internal;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.shop.cart.Cart;
import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.CartContents;
import org.mayocat.shop.cart.CartLoader;
import org.mayocat.shop.cart.CartManager;
import org.mayocat.shop.cart.InvalidCartOperationException;
import org.mayocat.shop.shipping.ShippingOption;
import org.mayocat.shop.taxes.Taxable;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultCartManager implements CartManager
{
    @Inject
    private CartAccessor cartAccessor;

    @Inject
    private CartLoader cartLoader;


    @Override
    public Cart getCart()
    {
        CartContents cart = cartAccessor.getCart();
        return cartLoader.load(cart);
    }

    @Override
    public void addItem(Taxable purchasable)
    {
        CartContents cart = cartAccessor.getCart();
        cart.addItem(purchasable);
        cartAccessor.setCart(cart);
    }

    @Override
    public void addItem(Taxable purchasable, Long quantity)
    {
        CartContents cart = cartAccessor.getCart();
        cart.addItem(purchasable, quantity);
        cartAccessor.setCart(cart);
    }

    @Override
    public void removeItem(Taxable purchasable) throws InvalidCartOperationException
    {
        CartContents cart = cartAccessor.getCart();
        cart.removeItem(purchasable);
        cartAccessor.setCart(cart);
    }

    @Override
    public void removeItem(Integer index) throws InvalidCartOperationException
    {
        CartContents cart = cartAccessor.getCart();
        Map<Taxable, Long> items = cart.getItems();
        Taxable toRemove = null;
        Integer i = 0;
        for (Taxable purchasable : items.keySet()) {
            if (i == index) {
                toRemove = purchasable;
            }
            i++;
        }
        if (toRemove != null) {
            removeItem(toRemove);
        }
    }

    @Override
    public void setQuantity(Taxable purchasable, Long quantity) throws InvalidCartOperationException
    {
        CartContents cart = cartAccessor.getCart();
        cart.setItem(purchasable, quantity);
        cartAccessor.setCart(cart);
    }

    @Override
    public void setQuantity(Integer index, Long quantity) throws InvalidCartOperationException
    {
        CartContents cart = cartAccessor.getCart();
        Map<Taxable, Long> items = cart.getItems();
        Taxable toUpdate = null;
        Integer i = 0;
        for (Taxable purchasable : items.keySet()) {
            if (i == index) {
                toUpdate = purchasable;
            }
            i++;
        }
        if (toUpdate != null) {
            setQuantity(toUpdate, quantity);
        }
    }

    @Override
    public void setSelectedShippingOption(ShippingOption option)
    {
        CartContents cart = cartAccessor.getCart();
        cart.setSelectedShippingOption(option);
        cartAccessor.setCart(cart);
    }

    @Override
    public void discardCart()
    {
        CartContents cart = cartAccessor.getCart();
        cart.empty();
        cartAccessor.setCart(cart);
    }

    /*
    private void recalculateShipping()
    {
        CartContents cart = cartAccessor.getCart();

        // In case shipping has been disabled or cart emptied
        if (!shippingService.isShippingEnabled() || cartIsEmpty(cart)) {
            cart.setSelectedShippingOption(null);
            cartAccessor.setCart(cart)
            return
        }

        if (cart.selectedShippingOption == null) {
            // Nothing else to do if we get there
            return
        }

        UUID selectedCarrierId = cart.selectedShippingOption.getCarrierId()
        cart.setSelectedShippingOption(shippingService.getOption(selectedCarrierId, cart.getItems()))

        cartAccessor.setCart(cart)
    }
    */
}
