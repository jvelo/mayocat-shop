/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.model.Association;
import org.mayocat.model.Entity;
import org.mayocat.model.annotation.Index;

/**
 * @version $Id$
 */
public class Order implements Entity
{
    public enum Status
    {
        /**
         * The order has no status
         */
        NONE,
        /**
         * Payment has been initialized (for example a credit card transaction has been initialized and we're waiting
         * for confirmation from the server
         */
        PAYMENT_PENDING,
        /**
         * The order is waiting for a future payment. Example: the customer has chosen a check payment method, and we're
         * waiting for the check
         */
        WAITING_FOR_PAYMENT,
        /**
         * Payment has failed (for example refused by an authorization server)
         */
        PAYMENT_FAILED,
        /**
         * The order has been paid and the payment is acknowledged
         */
        PAID,
        /**
         * The order is prepared for shipment
         */
        PREPARED,
        /**
         * The order is shipped
         */
        SHIPPED,
        /**
         * The order is cancelled
         */
        CANCELLED
    }

    public static final String ORDER_DATA_ID = "id";

    public static final String ORDER_DATA_ITEMS = "items";

    public static final String ORDER_DATA_SHIPPING = "shipping";

    private UUID id;

    private UUID customerId;

    private UUID deliveryAddressId;

    private UUID billingAddressId;

    @Index
    @NotNull
    @Size(min = 1)
    private String slug;

    private Date creationDate;

    private Date updateDate;

    private Currency currency;

    private Long numberOfItems;

    private BigDecimal itemsTotal;

    private BigDecimal shipping;

    private BigDecimal grandTotal;

    private Status status;

    private String additionalInformation;

    private Map<String, Object> orderData;

    private Association<Customer> customer = Association.notLoaded();

    private Association<Address> billingAddress = Association.notLoaded();

    private Association<Address> deliveryAddress = Association.notLoaded();

    @Override
    public UUID getId()
    {
        return this.id;
    }

    @Override
    public void setId(UUID id)
    {
        this.id = id;
    }

    @Override
    public String getSlug()
    {
        return this.slug;
    }

    @Override
    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate)
    {
        this.updateDate = updateDate;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    public Long getNumberOfItems()
    {
        return numberOfItems;
    }

    public void setNumberOfItems(Long numberOfItems)
    {
        this.numberOfItems = numberOfItems;
    }

    public BigDecimal getItemsTotal()
    {
        return itemsTotal;
    }

    public void setItemsTotal(BigDecimal itemsTotal)
    {
        this.itemsTotal = itemsTotal;
    }

    public BigDecimal getShipping()
    {
        return shipping;
    }

    public void setShipping(BigDecimal shipping)
    {
        this.shipping = shipping;
    }

    public BigDecimal getGrandTotal()
    {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal)
    {
        this.grandTotal = grandTotal;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public Map<String, Object> getOrderData()
    {
        return orderData;
    }

    public String getAdditionalInformation()
    {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation)
    {
        this.additionalInformation = additionalInformation;
    }

    public void setOrderData(Map<String, Object> orderData)
    {
        this.orderData = orderData;
    }

    public UUID getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(UUID customerId)
    {
        this.customerId = customerId;
    }

    public UUID getDeliveryAddressId()
    {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(UUID deliveryAddressId)
    {
        this.deliveryAddressId = deliveryAddressId;
    }

    public UUID getBillingAddressId()
    {
        return billingAddressId;
    }

    public void setBillingAddressId(UUID billingAddressId)
    {
        this.billingAddressId = billingAddressId;
    }

    public Association<Customer> getCustomer()
    {
        return customer;
    }

    public void setCustomer(Association<Customer> customer)
    {
        this.customer = customer;
    }

    public Association<Address> getBillingAddress()
    {
        return billingAddress;
    }

    public void setBillingAddress(Association<Address> billingAddress)
    {
        this.billingAddress = billingAddress;
    }

    public Association<Address> getDeliveryAddress()
    {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Association<Address> deliveryAddress)
    {
        this.deliveryAddress = deliveryAddress;
    }
}
