/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.internal;

import javax.inject.Inject;

import org.mayocat.shop.cart.CartInSessionConverter;
import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.cart.model.CartInSession;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.catalog.store.ProductStore;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultCartInSessionConverter implements CartInSessionConverter
{
    @Inject
    private ProductStore productStore;

    @Inject
    private Logger logger;

    @Override
    public CartInSession convertToCartInSession(Cart cart)
    {
        return new CartInSession(cart);
    }

    @Override
    public Cart loadFromCartInSession(CartInSession cartInSession)
    {
        Cart cart = new Cart(cartInSession.getCurrency());
        cart.setSelectedShippingOption(cartInSession.getSelectedOption());
        for (CartInSession.IdAndType idAndType : cartInSession.getItems().keySet()) {

            try {
                Purchasable p;
                Class clazz = Class.forName(idAndType.getType());

                // Poor-man's pattern matching...
                if (Product.class.isAssignableFrom(clazz)) {
                    p = productStore.findById(idAndType.getId());
                    if (p != null) {
                        cart.addItem(p, cartInSession.getItems().get(idAndType));
                    }
                } else {
                    // Purchasable not managed...
                }
            } catch (ClassNotFoundException e) {
                // Ignore...
            }
        }

        return cart;
    }
}
