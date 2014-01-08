/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.store.jdbi;

import javax.inject.Inject;

import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.payment.InvalidGatewayCustomerDataException;
import org.mayocat.shop.payment.model.GatewayCustomerData;
import org.mayocat.shop.payment.store.GatewayCustomerDataStore;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.google.common.base.Optional;

import mayoapp.dao.GatewayCustomerDataDAO;

/**
 * DBI implementation of {@link GatewayCustomerDataStore}
 *
 * @version $Id$
 */
@Component
public class DBIGatewayCustomerDataStore implements GatewayCustomerDataStore, Initializable
{
    @Inject
    private DBIProvider dbi;

    private GatewayCustomerDataDAO dao;

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
    public void storeGatewayCustomerData(GatewayCustomerData customerData)
            throws InvalidGatewayCustomerDataException
    {
        if (customerData.getCustomerId() == null || customerData.getGateway() == null) {
            throw new InvalidGatewayCustomerDataException();
        }

        if (dao.getCustomerData(customerData.getCustomerId(), customerData.getGateway()) == null) {
            this.dao.createCustomerData(customerData);
        } else {
            this.dao.updateCustomerData(customerData);
        }
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = dbi.get().onDemand(GatewayCustomerDataDAO.class);
    }
}
