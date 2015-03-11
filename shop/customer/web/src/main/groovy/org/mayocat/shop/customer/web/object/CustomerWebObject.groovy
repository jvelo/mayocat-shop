/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.web.object

import groovy.transform.CompileStatic
import org.mayocat.shop.customer.model.Address
import org.mayocat.shop.customer.model.Customer

/**
 * @version $Id$
 */
@CompileStatic
class CustomerWebObject
{
    String email

    String firstName

    String lastName

    String company

    String phone

    AddressWebObject deliveryAddress

    AddressWebObject billingAddress

    Customer toCustomer()
    {
        Customer customer = new Customer();

        customer.email = email
        customer.firstName = firstName
        customer.lastName = lastName
        customer.company = company
        customer.phoneNumber = phone

        customer
    }

    CustomerWebObject withCustomer(Customer customer)
    {
        email = customer.email
        firstName = customer.firstName
        lastName = customer.lastName
        company = customer.company
        phone = customer.phoneNumber

        this
    }

    CustomerWebObject withDeliveryAddress(Address address)
    {
        deliveryAddress = new AddressWebObject().withAddress(address)

        this
    }

    CustomerWebObject withBillingAddress(Address address)
    {
        billingAddress = new AddressWebObject().withAddress(address)

        this
    }
}
