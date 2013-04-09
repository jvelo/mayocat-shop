package org.mayocat.shop.cart;

import org.mayocat.shop.cart.model.Cart;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface CartAccessor
{
    public Cart getCart();
}
