/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.model;

import java.util.Map;
import java.util.UUID;

import org.mayocat.model.annotation.DoNotIndex;

/**
 * Customer data managed by a payment gateway.
 *
 * @version $Id$
 */
public class GatewayCustomerData extends GatewayData
{
    private UUID customerId;

    public GatewayCustomerData(UUID customerId, String gateway, Map<String, Object> data)
    {
        super(data, gateway);
        this.customerId = customerId;
    }

    public UUID getCustomerId()
    {
        return customerId;
    }
}
