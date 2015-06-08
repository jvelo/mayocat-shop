/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout.internal;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import java.util.Arrays;
import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.mayocat.shop.checkout.CheckoutRequest;
import org.mayocat.shop.customer.model.Address;
import org.mayocat.shop.customer.model.Customer;

/**
 * @version $Id$
 */
public class CheckoutRequestBuilder
{
    public CheckoutRequest build(final MultivaluedMap data) {
        CheckoutRequest request = new CheckoutRequest();
        String email = null;

        if (data.containsKey("email")) {
            email = (String) data.getFirst("email");
            EmailValidator emailValidator = EmailValidator.getInstance(false);
            if (!emailValidator.isValid(email)) {
                request.putError("email", new CheckoutRequest.Error(CheckoutRequest.ErrorType.BAD_VALUE, "email is not valid"));
            }
        } else {
            request.putError("email", new CheckoutRequest.Error(CheckoutRequest.ErrorType.REQUIRED, "email is mandatory"));
        }

        String firstName = getNonEmptyFieldValueOrAddToErrorMap("firstName", data, request);
        String lastName = getNonEmptyFieldValueOrAddToErrorMap("lastName", data, request);
        String street = getNonEmptyFieldValueOrAddToErrorMap("street", data, request);
        String zip = getNonEmptyFieldValueOrAddToErrorMap("zip", data, request);
        String city = getNonEmptyFieldValueOrAddToErrorMap("city", data, request);
        String country = getNonEmptyFieldValueOrAddToErrorMap("country", data, request);

        Address billingAddress = null;
        boolean hasDifferentBillingAddress =
                FluentIterable.from(Arrays.asList("street", "zip", "city", "country")).anyMatch(new Predicate<String>()
                {
                    public boolean apply(@Nullable String input) {
                        return data.containsKey("billing" + StringUtils.capitalize(input)) && StringUtils.isNotBlank(
                                (String) data.getFirst("billing" + StringUtils.capitalize(input)));
                    }
                });
        if (hasDifferentBillingAddress) {
            String billingStreet = getNonEmptyFieldValueOrAddToErrorMap("billingStreet", data, request);
            String billingZip = getNonEmptyFieldValueOrAddToErrorMap("billingZip", data, request);
            String billingCity = getNonEmptyFieldValueOrAddToErrorMap("billingCity", data, request);
            String billingCountry = getNonEmptyFieldValueOrAddToErrorMap("billingCountry", data, request);
            billingAddress = new Address();
            billingAddress.setFullName(firstName + " " + lastName);
            billingAddress.setStreet(billingStreet);
            billingAddress.setZip(billingZip);
            billingAddress.setCity(billingCity);
            billingAddress.setCountry(billingCountry);
            if (data.containsKey("billingCompany")) {
                String company = (String) data.getFirst("billingCompany");
                if (!Strings.isNullOrEmpty(company)) {
                    billingAddress.setCompany(company);
                }
                String streetComplement = (String) data.getFirst("billingStreetComplement");
                if (!Strings.isNullOrEmpty(streetComplement)) {
                    billingAddress.setStreetComplement(streetComplement);
                }
            }

            request.setBillingAddress(billingAddress);
        }

        if (request.getErrors().keySet().size() == 0) {
            Customer customer = new Customer();
            customer.setEmail(email);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            if (data.containsKey("phone") && !Strings.isNullOrEmpty((String) data.getFirst("phone"))) {
                customer.setPhoneNumber((String) data.getFirst("phone"));
            }

            request.setCustomer(customer);

            Address deliveryAddress = new Address();
            deliveryAddress.setFullName(firstName + " " + lastName);
            deliveryAddress.setStreet(street);
            deliveryAddress.setZip(zip);
            deliveryAddress.setCity(city);
            deliveryAddress.setCountry(country);
            if (data.containsKey("company")) {
                String company = (String) data.getFirst("company");
                if (!Strings.isNullOrEmpty(company)) {
                    deliveryAddress.setCompany(company);
                }
                String streetComplement = (String) data.getFirst("streetComplement");
                if (!Strings.isNullOrEmpty(streetComplement)) {
                    deliveryAddress.setStreetComplement(streetComplement);
                }
            }

            request.setDeliveryAddress(deliveryAddress);

            // Include additional information if the field is present and not empty
            if (data.containsKey("additionalInformation") &&
                    !Strings.isNullOrEmpty((String) data.getFirst("additionalInformation"))) {
                request.putOtherOrderData("additionalInformation", data.getFirst("additionalInformation"));
            }
        }

        return request;
    }

    private String getNonEmptyFieldValueOrAddToErrorMap(String field, MultivaluedMap data, CheckoutRequest request) {
        if (!data.containsKey(field) || Strings.isNullOrEmpty((String) data.getFirst(field))) {
            request.putError(field, new CheckoutRequest.Error(CheckoutRequest.ErrorType.REQUIRED, StringUtils.capitalize(field) + " is mandatory"));
            return null;
        } else {
            return (String) data.getFirst(field);
        }
    }
}
