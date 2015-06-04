/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout;

import java.util.Map;

import org.mayocat.shop.billing.model.Order;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class CheckoutResponse
{
    private Optional<String> redirectURL = Optional.absent();

    private Optional<String> formURL = Optional.absent();

    private Order order;

    private Map<String, Object> data = Maps.newHashMap();

    public Optional<String> getRedirectURL()
    {
        return redirectURL;
    }

    public void setRedirectURL(Optional<String> redirectURL)
    {
        this.redirectURL = redirectURL;
    }

    public Optional<String> getFormURL() {
        return formURL;
    }

    public void setFormURL(Optional<String> formURL) {
        this.formURL = formURL;
    }

    public Order getOrder()
    {
        return order;
    }

    public void setOrder(Order order)
    {
        this.order = order;
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public void setData(Map<String, Object> data)
    {
        this.data = data;
    }
}
