/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout;

import java.util.Map;
import java.util.UUID;

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

    CheckoutResponse checkout(Cart cart, Customer customer, Address deliveryAddress,
            Address billingAddress, Map<String, Object> otherOrderData) throws CheckoutException;

    void dropOrder(UUID orderId) throws CheckoutException;
}
