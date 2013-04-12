package org.mayocat.shop.checkout;

import javax.ws.rs.core.UriInfo;

import org.mayocat.shop.billing.model.Address;
import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.cart.model.Cart;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface CheckoutRegister
{
    boolean requiresForm();

    CheckoutResponse checkout(Cart cart, UriInfo uriInfo, Customer customer, Address deliveryAddress,
            Address billingAddress) throws CheckoutException;
}
