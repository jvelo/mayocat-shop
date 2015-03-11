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

/**
 * @version $Id$
 */
@CompileStatic
class AddressWebObject
{
    String company

    String fullName

    String street

    String streetComplement

    String zip

    String city

    String country

    String note

    Address toAddress()
    {
        Address address = new Address()

        address.company = company
        address.fullName = fullName
        address.street = street
        address.streetComplement = streetComplement
        address.zip = zip
        address.city = city
        address.country = country
        address.note = note

        address
    }

    AddressWebObject withAddress(Address address)
    {
        company = address.company
        fullName = address.fullName
        street = address.street
        streetComplement = address.streetComplement
        zip = address.zip
        city = address.city
        country = address.country
        note = address.note

        this
    }
}
