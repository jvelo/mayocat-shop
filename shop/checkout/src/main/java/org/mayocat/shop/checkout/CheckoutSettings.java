/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout;

import javax.validation.Valid;

import org.mayocat.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class CheckoutSettings
{
    @Valid
    @JsonProperty
    private Configurable<Boolean> guestCheckout = new Configurable<>(Boolean.TRUE);

    @Valid
    @JsonProperty
    private String defaultPaymentGateway = "paypaladaptivepayments";

    public String getDefaultPaymentGateway()
    {
        return defaultPaymentGateway;
    }

    public Configurable<Boolean> isGuestCheckoutEnabled()
    {
        return guestCheckout;
    }
}
