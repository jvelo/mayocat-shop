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

import org.mayocat.accounts.model.Tenant;
import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.payment.InvalidGatewayDataException;
import org.mayocat.shop.payment.model.GatewayCustomerData;
import org.mayocat.shop.payment.model.GatewayTenantData;
import org.mayocat.shop.payment.store.GatewayDataStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * In-memory implementation of {@link GatewayDataStore}
 *
 * @version $Id$
 */
@Component("memory")
public class MemoryGatewayDataStore implements GatewayDataStore
{
    static class Key
    {
        Key(UUID id, String gateway, String type)
        {
            this.id = id;
            this.gateway = gateway;
            this.type = type;
        }

        private UUID id;

        private String gateway;

        private String type;

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

            return Objects.equal(this.id, other.id)
                    && Objects.equal(this.gateway, other.gateway)
                    && Objects.equal(this.type, other.type);
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(
                    this.id,
                    this.gateway,
                    this.type
            );
        }
    }

    private Map<Key, Object> store = Maps.newConcurrentMap();

    @Override
    public Optional<GatewayCustomerData> getCustomerData(Customer customer, String gatewayId)
    {
        Key key = new Key(customer.getId(), gatewayId, "customer");
        if (store.containsKey(key)) {
            return Optional.of((GatewayCustomerData) store.get(key));
        }
        return Optional.absent();
    }

    @Override
    public void storeCustomerData(GatewayCustomerData customerData)
            throws InvalidGatewayDataException
    {
        Key key = new Key(customerData.getCustomerId(), customerData.getGateway(), "customer");
        this.store.put(key, customerData);
    }

    @Override
    public Optional<GatewayTenantData> getTenantData(Tenant tenant, String gatewayId)
    {
        Key key = new Key(tenant.getId(), gatewayId, "tenant");
        if (store.containsKey(key)) {
            return Optional.of((GatewayTenantData) store.get(key));
        }
        return Optional.absent();
    }

    @Override
    public void storeTenantData(GatewayTenantData tenantData) throws InvalidGatewayDataException
    {
        Key key = new Key(tenantData.getTenantId(), tenantData.getGateway(), "tenant");
        this.store.put(key, tenantData);
    }
}
