/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout;

import java.util.UUID;
import org.mayocat.shop.taxes.Taxable;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface CheckoutRegister
{
    /**
     * Cart checkout. Checks out the contents of the cart.
     *
     * @param request the checkout request with customer, delivery, billing information
     * @return the response from the checkout register
     * @throws CheckoutException when a problem occurs while checking out
     */
    CheckoutResponse checkoutCart(CheckoutRequest request) throws CheckoutException;

    /**
     * Direct checkout. Checks out a single product directly (by-passing the "cart").
     *
     * @param request the checkout request with customer, delivery, billing information
     * @param taxable the purchasable to checkout
     * @param quantity the quantity of purchasable to checkout
     * @return the response from the checkout register
     * @throws CheckoutException when a problem occurs while checking out
     */
    CheckoutResponse directCheckout(CheckoutRequest request, Taxable taxable, Long quantity) throws CheckoutException;

    void dropOrder(UUID orderId) throws CheckoutException;
}
