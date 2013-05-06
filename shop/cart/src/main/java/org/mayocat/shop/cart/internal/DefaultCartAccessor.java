package org.mayocat.shop.cart.internal;

import javax.inject.Inject;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.context.Execution;
import org.mayocat.session.Session;
import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.model.Cart;
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
    private Execution execution;

    @Override
    public Cart getCart()
    {
        Session session = this.execution.getContext().getSession();
        if (session.getAttribute(SESSION_CART_KEY) != null) {
            Cart cart = (Cart) session.getAttribute(SESSION_CART_KEY);
            LOGGER.debug("Retrieved cart from session with {} items", cart.getItems().keySet());
            return cart;
        }

        CatalogSettings catalogSettings = configurationService.getSettings(CatalogSettings.class);
        Cart cart = new Cart(catalogSettings.getCurrencies().getMainCurrency().getValue());
        session.setAttribute(SESSION_CART_KEY, cart);
        return cart;
    }
}
