/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout;

import org.mayocat.shop.billing.model.Order;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class CheckoutResponse
{
    private Optional<String> redirectURL = Optional.absent();

    private Order order;

    public Optional<String> getRedirectURL()
    {
        return redirectURL;
    }

    public void setRedirectURL(Optional<String> redirectURL)
    {
        this.redirectURL = redirectURL;
    }

    public Order getOrder()
    {
        return order;
    }

    public void setOrder(Order order)
    {
        this.order = order;
    }
}
