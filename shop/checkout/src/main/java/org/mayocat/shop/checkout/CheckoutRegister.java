package org.mayocat.shop.checkout;

import org.mayocat.shop.cart.model.Cart;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface CheckoutRegister
{
    void checkout(Cart cart, CustomerDetails customerDetails) throws CheckoutException;
}
