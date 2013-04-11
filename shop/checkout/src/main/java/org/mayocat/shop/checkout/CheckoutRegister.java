package org.mayocat.shop.checkout;

import javax.ws.rs.core.UriInfo;

import org.mayocat.shop.cart.model.Cart;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface CheckoutRegister
{
    boolean requiresForm();

    CheckoutResponse checkout(Cart cart, UriInfo uriInfo, CustomerDetails customerDetails) throws CheckoutException;
}
