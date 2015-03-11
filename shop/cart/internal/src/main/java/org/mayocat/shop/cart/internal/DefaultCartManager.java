/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.internal;

import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.mayocat.shop.cart.Cart;
import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.CartContents;
import org.mayocat.shop.cart.CartLoader;
import org.mayocat.shop.cart.CartManager;
import org.mayocat.shop.cart.InvalidCartOperationException;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.ShippingOption;
import org.mayocat.shop.shipping.ShippingService;
import org.mayocat.shop.taxes.Taxable;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Maps;

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

    @Inject
    private ShippingService shippingService;

    @Override
    public Cart getCart()
    {
        CartContents cart = cartAccessor.getCart();
        return cartLoader.load(cart);
    }

    @Override
    public Taxable getItem(Integer index)
    {
        CartContents cart = cartAccessor.getCart();
        Map<Taxable, Long> items = cart.getItems();
        Integer i = 0;
        for (Taxable purchasable : items.keySet()) {
            if (i == index) {
                return purchasable;
            }
            i++;
        }
        return null;
    }

    @Override
    public void addItem(Taxable purchasable)
    {
        CartContents cart = cartAccessor.getCart();
        cart.addItem(purchasable);
        cartAccessor.setCart(cart);

        this.recalculateShipping();
    }

    @Override
    public void addItem(Taxable purchasable, Long quantity)
    {
        CartContents cart = cartAccessor.getCart();
        cart.addItem(purchasable, quantity);
        cartAccessor.setCart(cart);

        this.recalculateShipping();
    }

    @Override
    public void removeItem(Taxable purchasable) throws InvalidCartOperationException
    {
        CartContents cart = cartAccessor.getCart();
        cart.removeItem(purchasable);
        cartAccessor.setCart(cart);

        this.recalculateShipping();
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
    public void setItem(Taxable newItem, Integer index) throws InvalidCartOperationException
    {
        CartContents cart = cartAccessor.getCart();
        Map<Taxable, Long> items = cart.getItems();
        Integer i = 0;
        Long quantity = null;
        Taxable toReplace = null;
        for (Taxable purchasable : items.keySet()) {
            if (i == index) {
                quantity = items.get(purchasable);
                toReplace = purchasable;
            }
            i++;
        }
        if (quantity != null && toReplace != null) {
            cart.replaceItem(toReplace, newItem, quantity);
            cartAccessor.setCart(cart);
        }
        else {
            throw new InvalidCartOperationException();
        }

        this.recalculateShipping();
    }

    @Override
    public void setQuantity(Taxable purchasable, Long quantity) throws InvalidCartOperationException
    {
        CartContents cart = cartAccessor.getCart();
        cart.setItem(purchasable, quantity);
        cartAccessor.setCart(cart);

        this.recalculateShipping();
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

        this.recalculateShipping();
    }

    @Override
    public void discardCart()
    {
        CartContents cart = cartAccessor.getCart();
        cart.empty();
        cart.setSelectedShippingOption(null);
        cartAccessor.setCart(cart);
    }

    private void recalculateShipping()
    {
        CartContents cart = cartAccessor.getCart();

        // In case shipping has been disabled or cart emptied
        if (!shippingService.isShippingEnabled() || cart.isEmpty()) {
            cart.setSelectedShippingOption(null);
            cartAccessor.setCart(cart);
            return;
        }

        if (cart.getSelectedShippingOption() == null) {
            // Nothing else to do if we get there
            return;
        }

        UUID selectedCarrierId = cart.getSelectedShippingOption().getCarrierId();
        Map<Purchasable, Long> itemsAsPurchasable = Maps.newHashMap();
        for (Taxable taxable : cart.getItems().keySet()) {
            itemsAsPurchasable.put(taxable, cart.getItems().get(taxable));
        }
        cart.setSelectedShippingOption(shippingService.getOption(selectedCarrierId, itemsAsPurchasable));

        cartAccessor.setCart(cart);
    }
}
