/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.store;

import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.payment.InvalidGatewayCustomerDataException;
import org.mayocat.shop.payment.model.GatewayCustomerData;

import com.google.common.base.Optional;

/**
 * Store interface for accessing {@link GatewayCustomerData}.
 *
 * @version $Id$
 */
public interface GatewayCustomerDataStore
{
    Optional<GatewayCustomerData> getCustomerData(Customer customer, String gatewayId);

    void storeGatewayCustomerData(GatewayCustomerData customerData) throws InvalidGatewayCustomerDataException;
}
