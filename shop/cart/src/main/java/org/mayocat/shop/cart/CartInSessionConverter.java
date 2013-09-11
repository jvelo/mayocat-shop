package org.mayocat.shop.cart;

import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.cart.model.CartInSession;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface CartInSessionConverter
{
    CartInSession convertToCartInSession(Cart cart);

    Cart loadFromCartInSession(CartInSession cartInSession);
}
