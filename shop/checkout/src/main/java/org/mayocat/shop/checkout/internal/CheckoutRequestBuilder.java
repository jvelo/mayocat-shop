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

        String firstName = multimapContains(data, "firstName") ? (String) data.getFirst("firstName") : null;
        String lastName = multimapContains(data, "lastName") ? (String) data.getFirst("lastName") : null;
        String company = multimapContains(data, "company") ? (String) data.getFirst("company") : null;

        if ((firstName == null || lastName == null) && company == null) {
            // Full name is only required when no company name has been provided
            if (firstName == null) {
                request.putError("firstName", new CheckoutRequest.Error(CheckoutRequest.ErrorType.REQUIRED, "First name is mandatory"));
            }
            if (lastName == null) {
                request.putError("lastName", new CheckoutRequest.Error(CheckoutRequest.ErrorType.REQUIRED, "Last name is mandatory"));
            }
        }

        String street = getNonEmptyFieldValueOrAddToErrorMap("street", data, request);
        String zip = getNonEmptyFieldValueOrAddToErrorMap("zip", data, request);
        String city = getNonEmptyFieldValueOrAddToErrorMap("city", data, request);
        String country = getNonEmptyFieldValueOrAddToErrorMap("country", data, request);

        Address billingAddress = null;
        boolean hasDifferentBillingAddress =
                FluentIterable.from(Arrays.asList("street", "zip", "city", "country")).anyMatch(new Predicate<String>() {
                    public boolean apply(@Nullable String input) {
                        return multimapContains(data, "billing" + StringUtils.capitalize(input));
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
            if (multimapContains(data, "billingCompany")) {
                billingAddress.setCompany((String) data.getFirst("billingCompany"));
            }
            if (multimapContains(data, "billingStreetComplement")) {
                billingAddress.setStreetComplement((String) data.getFirst("billingStreetComplement"));
            }
            request.setBillingAddress(billingAddress);
        }

        if (request.getErrors().keySet().size() == 0) {
            Customer customer = new Customer();
            customer.setEmail(email);
            if (firstName != null) {
                customer.setFirstName(firstName);
            }
            if (lastName != null) {
                customer.setLastName(lastName);
            }
            if (multimapContains(data, "phone")) {
                customer.setPhoneNumber((String) data.getFirst("phone"));
            }
            if (company != null) {
                customer.setCompany(company);
            }

            request.setCustomer(customer);

            Address deliveryAddress = new Address();
            deliveryAddress.setFullName(firstName + " " + lastName);
            deliveryAddress.setStreet(street);
            deliveryAddress.setZip(zip);
            deliveryAddress.setCity(city);
            deliveryAddress.setCountry(country);
            if (company != null) {
                // FIXME this should be a different property than the customer one
                deliveryAddress.setCompany(company);
            }
            if (multimapContains(data, "streetComplement")) {
                deliveryAddress.setStreetComplement((String) data.getFirst("streetComplement"));
            }

            request.setDeliveryAddress(deliveryAddress);

            // Include additional information if the field is present and not empty
            if (multimapContains(data, "additionalInformation")) {
                request.putOtherOrderData("additionalInformation", data.getFirst("additionalInformation"));
            }
        }
        return request;
    }

    private static String getNonEmptyFieldValueOrAddToErrorMap(String field, MultivaluedMap data, CheckoutRequest request) {
        if (!multimapContains(data, field)) {
            request.putError(field, new CheckoutRequest.Error(CheckoutRequest.ErrorType.REQUIRED,
                    StringUtils.capitalize(field) + " is mandatory"));
            return null;
        } else {
            return (String) data.getFirst(field);
        }
    }

    private static boolean multimapContains(MultivaluedMap data, String field) {
        return data.containsKey(field) && !Strings.isNullOrEmpty((String) data.getFirst(field));
    }
}
