/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.internal;

import javax.inject.Inject;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.context.WebContext;
import org.mayocat.context.scope.Session;
import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.CartInSessionConverter;
import org.mayocat.shop.cart.CartContents;
import org.mayocat.shop.cart.CartInSession;
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultCartAccessor implements CartAccessor
{
    public static final String SESSION_CART_KEY = "org.mayocat.shop.cart.front.Cart";

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(DefaultCartAccessor.class);

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private WebContext context;

    @Inject
    private CartInSessionConverter cartInSessionConverter;

    @Override
    public CartContents getCart()
    {
        Session session = this.context.getSession();
        if (session.getAttribute(SESSION_CART_KEY) != null) {
            try {
                CartInSession cartInSession = (CartInSession) session.getAttribute(SESSION_CART_KEY);
                CartContents cartContents = cartInSessionConverter.loadFromCartInSession(cartInSession);
                LOGGER.debug("Retrieved cart from session with {} items", cartContents.getItems().keySet());
                return cartContents;
            } catch (ClassCastException e) {
                // Backward compatibility : the session attribute use to contain the cart directly.
                // We do nothing, the code below will create a new cart.
            }
        }

        CatalogSettings catalogSettings = configurationService.getSettings(CatalogSettings.class);
        CartContents cartContents = new CartContents(catalogSettings.getCurrencies().getMainCurrency().getValue());
        session.setAttribute(SESSION_CART_KEY, cartInSessionConverter.convertToCartInSession(cartContents));
        return cartContents;
    }

    @Override
    public void setCart(CartContents cartContents)
    {
        Session session = this.context.getSession();
        if (cartContents.isEmpty()) {
            // We store the cart in cookies only if it's not empty... Otherwise we will create a new cart when needed
            // (maybe the currency has change? etc.)
            session.removeAttribute(SESSION_CART_KEY);
        } else {
            session.setAttribute(SESSION_CART_KEY, cartInSessionConverter.convertToCartInSession(cartContents));
        }
    }
}
