package org.mayocat.shop.checkout.internal;

import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.checkout.CheckoutException;
import org.mayocat.shop.checkout.CheckoutRegister;
import org.mayocat.shop.checkout.CustomerDetails;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultCheckoutRegister implements CheckoutRegister
{
    @Override
    public void checkout(Cart cart, CustomerDetails customerDetails) throws CheckoutException
    {

    }
}
