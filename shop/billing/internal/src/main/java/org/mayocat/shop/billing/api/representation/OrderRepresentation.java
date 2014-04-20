/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.api.representation;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mayocat.shop.billing.model.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @version $Id$
 */
public class OrderRepresentation
{
    private String slug;

    private DateTime creationDate;

    private DateTime updateDate;

    private Currency currency;

    private Long numberOfItems;

    private BigDecimal itemsTotal;

    private BigDecimal shipping;

    private BigDecimal grandTotal;

    private Order.Status status;

    private String additionalInformation;

    private Map<String, Object> data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CustomerRepresentation customer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AddressRepresentation deliveryAddress;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AddressRepresentation billingAddress;

    public OrderRepresentation()
    {
        // Empty representation for Jackson de-serialization
    }

    public OrderRepresentation(DateTimeZone tenantZone, Order order)
    {
        this.slug = order.getSlug();
        if (order.getCreationDate() != null) {
            this.creationDate = new DateTime(order.getCreationDate().getTime(), tenantZone);
        }
        if (order.getUpdateDate() != null) {
            this.updateDate = new DateTime(order.getUpdateDate().getTime(), tenantZone);
        }

        this.currency = order.getCurrency();
        this.numberOfItems = order.getNumberOfItems();
        this.itemsTotal = order.getItemsTotal();
        this.shipping = order.getShipping();
        this.grandTotal = order.getGrandTotal();
        this.status = order.getStatus();
        this.additionalInformation = order.getAdditionalInformation();
        this.data = order.getOrderData();

        if (order.getBillingAddress().isLoaded()) {
            this.setBillingAddress(new AddressRepresentation(order.getBillingAddress().get()));
        }
        if (order.getDeliveryAddress().isLoaded()) {
            this.setDeliveryAddress(new AddressRepresentation(order.getDeliveryAddress().get()));
        }
        if (order.getCustomer().isLoaded()) {
            this.setCustomer(new CustomerRepresentation(order.getCustomer().get()));
        }
    }

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public DateTime getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate)
    {
        this.creationDate = creationDate;
    }

    public DateTime getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate(DateTime updateDate)
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

    public Order.Status getStatus()
    {
        return status;
    }

    public void setStatus(Order.Status status)
    {
        this.status = status;
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public String getAdditionalInformation()
    {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation)
    {
        this.additionalInformation = additionalInformation;
    }

    public void setData(Map<String, Object> data)
    {
        this.data = data;
    }

    public CustomerRepresentation getCustomer()
    {
        return customer;
    }

    public void setCustomer(CustomerRepresentation customer)
    {
        this.customer = customer;
    }

    public AddressRepresentation getDeliveryAddress()
    {
        return deliveryAddress;
    }

    public void setDeliveryAddress(AddressRepresentation deliveryAddress)
    {
        this.deliveryAddress = deliveryAddress;
    }

    public AddressRepresentation getBillingAddress()
    {
        return billingAddress;
    }

    public void setBillingAddress(AddressRepresentation billingAddress)
    {
        this.billingAddress = billingAddress;
    }
}
