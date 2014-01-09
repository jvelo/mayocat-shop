/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.model;

import java.util.Map;

/**
 * Gateway data common to {@link GatewayCustomerData} and {@link GatewayTenantData}.
 *
 * @version $Id$
 */
public class GatewayData
{
    protected String gateway;

    protected Map<String, Object> data;

    public GatewayData(Map<String, Object> data, String gateway)
    {
        this.data = data;
        this.gateway = gateway;
    }

    public String getGateway()
    {
        return gateway;
    }

    public Map<String, Object> getData()
    {
        return data;
    }
}
