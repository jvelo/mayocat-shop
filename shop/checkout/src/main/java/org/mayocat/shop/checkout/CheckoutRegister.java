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

import org.mayocat.shop.customer.model.Address;
import org.mayocat.shop.customer.model.Customer;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface CheckoutRegister
{
    boolean requiresForm();

    CheckoutResponse checkout(Customer customer, Address deliveryAddress,
            Address billingAddress, Map<String, Object> otherOrderData) throws CheckoutException;

    void dropOrder(UUID orderId) throws CheckoutException;
}
