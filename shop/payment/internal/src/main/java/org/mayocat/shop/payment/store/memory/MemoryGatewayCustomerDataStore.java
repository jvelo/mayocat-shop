/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.store.memory;

import java.util.Map;
import java.util.UUID;

import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.payment.InvalidGatewayCustomerDataException;
import org.mayocat.shop.payment.model.GatewayCustomerData;
import org.mayocat.shop.payment.store.GatewayCustomerDataStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * In-memory implementation of {@link }
 *
 * @version $Id$
 */
@Component("memory")
public class MemoryGatewayCustomerDataStore implements GatewayCustomerDataStore
{
    static class Key
    {
        Key(UUID customerId, String gateway)
        {
            this.customerId = customerId;
            this.gateway = gateway;
        }

        private UUID customerId;

        private String gateway;

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Key other = (Key) obj;

            return Objects.equal(this.customerId, other.customerId)
                    && Objects.equal(this.gateway, other.gateway);
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(
                    this.customerId,
                    this.gateway
            );
        }
    }

    private Map<Key, GatewayCustomerData> data = Maps.newConcurrentMap();

    @Override
    public Optional<GatewayCustomerData> getCustomerData(Customer customer, String gatewayId)
    {
        Key key = new Key(customer.getId(), gatewayId);
        if (data.containsKey(key)) {
            return Optional.of(data.get(key));
        }
        return Optional.absent();
    }

    @Override
    public void storeGatewayCustomerData(GatewayCustomerData customerData)
            throws InvalidGatewayCustomerDataException
    {
        Key key = new Key(customerData.getCustomerId(), customerData.getGateway());
        this.data.put(key, customerData);
    }
}
