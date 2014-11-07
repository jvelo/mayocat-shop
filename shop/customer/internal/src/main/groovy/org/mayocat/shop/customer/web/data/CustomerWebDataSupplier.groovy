/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.web.data

import groovy.transform.CompileStatic
import org.mayocat.accounts.web.object.UserWebObject
import org.mayocat.context.WebContext
import org.mayocat.shop.customer.model.Address
import org.mayocat.shop.customer.model.Customer
import org.mayocat.shop.customer.store.AddressStore
import org.mayocat.shop.customer.store.CustomerStore
import org.mayocat.shop.customer.web.object.CustomerWebObject
import org.mayocat.shop.front.WebDataSupplier
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider

/**
 * @version $Id$
 */
@CompileStatic
@Component("customerWebDataSupplier")
class CustomerWebDataSupplier implements WebDataSupplier
{
    @Inject
    Provider<CustomerStore> customerStore

    @Inject
    Provider<AddressStore> addressStore

    @Inject
    WebContext context

    @Override
    void supply(Map<String, Object> data)
    {
        if (context.user) {
            Customer customer = customerStore.get().findByUserId(context.user.id)

            if (customer == null) {
                return
            }

            CustomerWebObject customerWebObject = new CustomerWebObject().withCustomer(customer)

            Address delivery = addressStore.get().findByCustomerIdAndType(customer.id, "delivery");
            if (delivery) {
                customerWebObject.withDeliveryAddress(delivery)
            }

            Address billing = addressStore.get().findByCustomerIdAndType(customer.id, "billing");
            if (billing) {
                customerWebObject.withBillingAddress(billing)
            }

            if (customer) {
                data.put("customer", customerWebObject)
            }
        }
    }
}
