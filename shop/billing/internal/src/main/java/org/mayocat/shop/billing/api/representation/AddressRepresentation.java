/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.api.representation;

import org.mayocat.shop.billing.model.Address;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @version $Id$
 */
public class AddressRepresentation
{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String company;

    private String fullName;

    private String street;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String streetComplement;

    private String zip;

    private String city;

    private String country;

    public AddressRepresentation()
    {

    }

    public AddressRepresentation(Address address)
    {
        this.company = address.getCompany();
        this.fullName = address.getFullName();
        this.street = address.getStreet();
        this.streetComplement = address.getStreetComplement();
        this.zip = address.getZip();
        this.city = address.getCity();
        this.country = address.getCountry();
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getStreetComplement()
    {
        return streetComplement;
    }

    public void setStreetComplement(String streetComplement)
    {
        this.streetComplement = streetComplement;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }
}
