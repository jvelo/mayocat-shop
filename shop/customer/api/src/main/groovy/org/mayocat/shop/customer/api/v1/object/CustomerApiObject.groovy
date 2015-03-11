/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.api.v1.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.configuration.PlatformSettings
import org.mayocat.model.AddonGroup
import org.mayocat.rest.api.object.AddonGroupApiObject
import org.mayocat.rest.api.object.BaseApiObject
import org.mayocat.shop.customer.model.Address
import org.mayocat.shop.customer.model.Customer
import org.mayocat.theme.ThemeDefinition

import static org.mayocat.rest.api.object.AddonGroupApiObject.forAddonGroup
import static org.mayocat.rest.api.object.AddonGroupApiObject.toAddonGroupMap

/**
 * @version $Id$
 */
@CompileStatic
class CustomerApiObject extends BaseApiObject
{
    String slug

    String email

    String firstName

    String lastName

    String company

    String phone

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, AddonGroupApiObject> addons

    Map<String, Object> _embedded

    CustomerApiObject withCustomer(Customer customer)
    {
        this.slug = customer.slug
        this.email = customer.email
        this.firstName = customer.firstName
        this.lastName = customer.lastName
        this.company = customer.company
        this.phone = customer.phoneNumber

        this
    }

    Customer toCustomer(PlatformSettings platformSettings, Optional<ThemeDefinition> themeDefinition)
    {
        Customer customer = new Customer();
        customer.slug = slug
        customer.email = email
        customer.firstName = firstName
        customer.lastName = lastName
        customer.company = company
        customer.phoneNumber = phone

        if (addons) {
            customer.addons = toAddonGroupMap(addons, platformSettings, themeDefinition)
        }

        customer
    }

    @JsonIgnore
    CustomerApiObject withAddons(Map<String, AddonGroup> entityAddons) {
        if (!addons) {
            addons = [:]
        }

        entityAddons.values().each({ AddonGroup addon ->
            addons.put(addon.group, forAddonGroup(addon))
        })

        this
    }


    CustomerApiObject withEmbeddedDeliveryAddress(Address address)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        _embedded.put("deliveryAddress", new AddressApiObject().withAddress(address))

        this
    }

    CustomerApiObject withEmbeddedBillingAddress(Address address)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        _embedded.put("billingAddress", new AddressApiObject().withAddress(address))

        this
    }
}
