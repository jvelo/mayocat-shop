package org.mayocat.shop.billing.api.representation;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mayocat.rest.jackson.DateTimeISO8601Deserializer;
import org.mayocat.rest.jackson.DateTimeISO8601Serializer;
import org.mayocat.shop.billing.model.Order;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @version $Id$
 */
public class OrderRepresentation
{
    private String slug;

    @JsonSerialize(using = DateTimeISO8601Serializer.class)
    @JsonDeserialize(using = DateTimeISO8601Deserializer.class)
    private DateTime creationDate;

    @JsonSerialize(using = DateTimeISO8601Serializer.class)
    @JsonDeserialize(using = DateTimeISO8601Deserializer.class)
    private DateTime updateDate;

    private Currency currency;

    private Long numberOfItems;

    private BigDecimal itemsTotal;

    private BigDecimal grandTotal;

    private Order.Status status;

    private Map<String, Object> orderData;

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
        this.grandTotal = order.getGrandTotal();
        this.status = order.getStatus();
        this.orderData = order.getOrderData();
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

    public Map<String, Object> getOrderData()
    {
        return orderData;
    }

    public void setOrderData(Map<String, Object> orderData)
    {
        this.orderData = orderData;
    }
}
