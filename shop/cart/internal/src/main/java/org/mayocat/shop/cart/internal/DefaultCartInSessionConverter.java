/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.internal;

import java.util.Currency;

import javax.inject.Inject;

import org.mayocat.shop.cart.CartInSessionConverter;
import org.mayocat.shop.cart.CartContents;
import org.mayocat.shop.cart.CartInSession;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.taxes.Taxable;
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
    public CartInSession convertToCartInSession(CartContents cartContents)
    {
        return new CartInSession(cartContents);
    }

    @Override
    public CartContents loadFromCartInSession(CartInSession cartInSession)
    {
        CartContents cartContents = new CartContents(Currency.getInstance(cartInSession.getCurrency()));
        cartContents.setSelectedShippingOption(cartInSession.getSelectedOption());
        for (CartInSession.IdAndType idAndType : cartInSession.getItems().keySet()) {

            try {
                Class clazz = Class.forName(idAndType.getType());

                // Poor-man's pattern matching...
                if (Product.class.isAssignableFrom(clazz)) {
                    Product product = productStore.findById(idAndType.getId());
                    if (product.getParentId() != null) {
                        Product parent = productStore.findById(product.getParentId());
                        if (parent == null) {
                            // parent set but not found -> ignore
                            continue;
                        }
                        product.setParent(parent);
                    }
                    if (product != null) {
                        try {
                            cartContents.addItem(product, cartInSession.getItems().get(idAndType));
                        } catch (Exception e) {
                            // Don't fail when a product can't be added back to the cart
                            logger.warn("Failed to add back product [{}] from session", product.getId());
                        }
                    }
                } else {
                    // Purchasable not managed...
                }
            } catch (ClassNotFoundException e) {
                // Ignore...
            }
        }

        return cartContents;
    }
}
