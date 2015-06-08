/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.Maps;
import java.util.Map;
import org.mayocat.shop.customer.model.Address;
import org.mayocat.shop.customer.model.Customer;

/**
 * @version $Id$
 */
public class CheckoutRequest
{
    public enum ErrorType
    {
        REQUIRED,
        BAD_VALUE;

        @JsonValue
        public String toJson() {
            return name().toLowerCase();
        }
    }

    public static class Error
    {
        private String userMessage;

        private ErrorType errorType;

        public Error(ErrorType type, String userMessage) {
            this.errorType = type;
            this.userMessage = userMessage;
        }

        public String getUserMessage() {
            return userMessage;
        }

        public ErrorType getErrorType() {
            return errorType;
        }
    }

    private Map<String, Error> errors = Maps.newHashMap();
    private Address billingAddress;
    private Customer customer;
    private Address deliveryAddress;
    private Map<String, Object> otherOrderData = Maps.newHashMap();

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void putError(String key, Error error) {
        this.errors.put(key, error);
    }

    public Map<String, Error> getErrors() {
        return errors;
    }

    public void putOtherOrderData(String key, Object data) {
        this.otherOrderData.put(key, data);
    }

    public Map<String, Object> getOtherOrderData() {
        return otherOrderData;
    }
}
