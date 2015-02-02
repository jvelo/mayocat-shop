/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.api.v1.object

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.mayocat.shop.billing.model.Order
import org.mayocat.shop.billing.model.OrderItem
import org.mayocat.shop.billing.model.OrderSummary
import org.mayocat.shop.customer.api.v1.object.AddressApiObject
import org.mayocat.shop.customer.api.v1.object.CustomerApiObject
import org.mayocat.shop.customer.model.Address
import org.mayocat.shop.customer.model.Customer
import org.mayocat.shop.payment.model.PaymentOperation

/**
 * @version $Id$
 */
@CompileStatic
class OrderApiObject
{
    String slug;

    DateTime creationDate;

    DateTime updateDate;

    Currency currency;

    Long numberOfItems;

    BigDecimal itemsTotal;

    BigDecimal shipping;

    BigDecimal grandTotal;

    OrderSummary.Status status;

    String additionalInformation;

    Map<String, Object> data;

    List<OrderItemApiObject> items = [];

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> _embedded

    OrderApiObject withOrder(Order order, DateTimeZone timeZone)
    {
        this.slug = order.slug
        if (order.creationDate) {
            this.creationDate = new DateTime(order.creationDate.time, timeZone);
        }
        if (order.updateDate) {
            this.updateDate = new DateTime(order.updateDate.time, timeZone);
        }

        this.currency = order.currency
        this.numberOfItems = order.numberOfItems
        this.itemsTotal = order.itemsTotal
        this.shipping = order.shipping
        this.grandTotal = order.grandTotal
        this.status = order.status
        this.additionalInformation = order.additionalInformation
        this.data = order.orderData

        items = order.orderItems.collect({ OrderItem item -> new OrderItemApiObject().withOrderItem(item) })

        this
    }

    OrderApiObject withEmbeddedCustomer(Customer customer)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        CustomerApiObject customerApiObject = new CustomerApiObject().withCustomer(customer)
        if (customer.addons.isLoaded()) {
            customerApiObject.withAddons(customer.addons.get())
        }
        _embedded.put("customer", customerApiObject)

        this
    }

    OrderApiObject withEmbeddedDeliveryAddress(Address address)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        _embedded.put("deliveryAddress", new AddressApiObject().withAddress(address))

        this
    }

    OrderApiObject withEmbeddedBillingAddress(Address address)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        _embedded.put("billingAddress", new AddressApiObject().withAddress(address))

        this
    }

    OrderApiObject withEmbeddedPaymentOperations(List<PaymentOperation> operations)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        _embedded.operations = operations.collect({ PaymentOperation operation ->
            new PaymentOperationApiObject().withPaymentOperation(operation)
        })

        this
    }
}
