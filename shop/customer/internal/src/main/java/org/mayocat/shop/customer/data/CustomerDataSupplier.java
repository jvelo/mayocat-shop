/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.data;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.accounts.UserDataSupplier;
import org.mayocat.accounts.model.User;
import org.mayocat.shop.customer.model.Address;
import org.mayocat.shop.customer.model.Customer;
import org.mayocat.shop.customer.store.AddressStore;
import org.mayocat.shop.customer.store.CustomerStore;
import org.mayocat.shop.customer.web.object.CustomerWebObject;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("customer")
public class CustomerDataSupplier implements UserDataSupplier
{
    @Inject
    private Provider<CustomerStore> customerStore;

    @Inject
    private Provider<AddressStore> addressStore;

    @Override
    public void supply(User user, Map<String, Object> data)
    {
        Customer customer = customerStore.get().findByUserId(user.getId());

        if (customer == null) {
            return;
        }

        CustomerWebObject customerWebObject = new CustomerWebObject().withCustomer(customer);

        Address delivery = addressStore.get().findByCustomerIdAndType(customer.getId(), "delivery");
        if (delivery != null) {
            customerWebObject.withDeliveryAddress(delivery);
        }

        Address billing = addressStore.get().findByCustomerIdAndType(customer.getId(), "billing");
        if (billing != null) {
            customerWebObject.withBillingAddress(billing);
        }

        if (customer != null) {
            data.put("customer", customerWebObject);
        }
    }
}
