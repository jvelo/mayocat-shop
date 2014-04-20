/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.store.jdbi;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.payment.InvalidGatewayDataException;
import org.mayocat.shop.payment.model.GatewayCustomerData;
import org.mayocat.shop.payment.model.GatewayTenantData;
import org.mayocat.shop.payment.store.GatewayDataStore;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.google.common.base.Optional;

import mayoapp.dao.GatewayDataDAO;

/**
 * DBI implementation of {@link org.mayocat.shop.payment.store.GatewayDataStore}
 *
 * @version $Id$
 */
@Component
public class DBIGatewayDataStore implements GatewayDataStore, Initializable
{
    @Inject
    private DBIProvider dbi;

    private GatewayDataDAO dao;

    @Override
    public Optional<GatewayCustomerData> getCustomerData(Customer customer, String gatewayId)
    {
        GatewayCustomerData result = dao.getCustomerData(customer.getId(), gatewayId);
        if (result == null) {
            return Optional.absent();
        }
        return Optional.of(result);
    }

    @Override
    public void storeCustomerData(GatewayCustomerData customerData)
            throws InvalidGatewayDataException
    {
        if (customerData.getCustomerId() == null || customerData.getGateway() == null) {
            throw new InvalidGatewayDataException();
        }

        if (dao.getCustomerData(customerData.getCustomerId(), customerData.getGateway()) == null) {
            this.dao.createCustomerData(customerData);
        } else {
            this.dao.updateCustomerData(customerData);
        }
    }

    @Override
    public Optional<GatewayTenantData> getTenantData(Tenant tenant, String gatewayId)
    {
        GatewayTenantData result = dao.getTenantData(tenant.getId(), gatewayId);
        if (result == null) {
            return Optional.absent();
        }
        return Optional.of(result);
    }

    @Override
    public void storeTenantData(GatewayTenantData tenantData) throws InvalidGatewayDataException
    {
        if (tenantData.getTenantId() == null || tenantData.getGateway() == null) {
            throw new InvalidGatewayDataException();
        }

        if (dao.getCustomerData(tenantData.getTenantId(), tenantData.getGateway()) == null) {
            this.dao.createTenantData(tenantData);
        } else {
            this.dao.updateTenantData(tenantData);
        }
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = dbi.get().onDemand(GatewayDataDAO.class);
    }
}
