/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.api.v1.object

import groovy.transform.CompileStatic
import org.mayocat.shop.customer.model.Address
import org.mayocat.shop.customer.model.Customer

/**
 * @version $Id$
 */
@CompileStatic
class CustomerApiObject
{
    String email

    String firstName

    String lastName

    String company

    String phone

    Map<String, Object> _embedded

    CustomerApiObject withCustomer(Customer customer)
    {
        this.email = customer.email
        this.firstName = customer.firstName
        this.lastName = customer.lastName
        this.company = customer.company
        this.phone = customer.phoneNumber

        this
    }

    CustomerApiObject withDeliveryAddress(Address address)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        _embedded.put("deliveryAddress", new AddressApiObject().withAddress(address))

        this
    }

    CustomerApiObject withBillingAddress(Address address)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        _embedded.put("billingAddress", new AddressApiObject().withAddress(address))

        this
    }
}
