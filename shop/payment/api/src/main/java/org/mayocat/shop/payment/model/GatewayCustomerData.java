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
public class GatewayCustomerData
{
    private UUID customerId;

    private String gateway;

    private Map<String, Object> data;

    public GatewayCustomerData(UUID customerId, String gateway, Map<String, Object> data)
    {
        this.customerId = customerId;
        this.gateway = gateway;
        this.data = data;
    }

    public UUID getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(UUID customerId)
    {
        this.customerId = customerId;
    }

    public String getGateway()
    {
        return gateway;
    }

    public void setGateway(String gateway)
    {
        this.gateway = gateway;
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
