package org.mayocat.shop.billing.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.model.Entity;
import org.mayocat.model.PerhapsLoaded;
import org.mayocat.model.annotation.SearchIndex;

/**
 * @version $Id$
 */
public class Order implements Entity
{
    public enum Status
    {
        NONE,
        WAITING_FOR_PAYMENT,
        PAYMENT_FAILED,
        PAID,
        PREPARED,
        SHIPPED,
        CANCELLED
    }

    private UUID id;

    private UUID customerId;

    private UUID deliveryAddressId;

    private UUID billingAddressId;

    @SearchIndex
    @NotNull
    @Size(min = 1)
    private String slug;

    private Date creationDate;

    private Date updateDate;

    private Currency currency;

    private Long numberOfItems;

    private BigDecimal itemsTotal;

    private BigDecimal grandTotal;

    private Status status;

    private Map<String, Object> orderData;

    private PerhapsLoaded<Customer> customer = PerhapsLoaded.notLoaded();

    private PerhapsLoaded<Address> billingAddress = PerhapsLoaded.notLoaded();

    private PerhapsLoaded<Address> deliveryAddress = PerhapsLoaded.notLoaded();

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

    public PerhapsLoaded<Customer> getCustomer()
    {
        return customer;
    }

    public void setCustomer(PerhapsLoaded<Customer> customer)
    {
        this.customer = customer;
    }

    public PerhapsLoaded<Address> getBillingAddress()
    {
        return billingAddress;
    }

    public void setBillingAddress(PerhapsLoaded<Address> billingAddress)
    {
        this.billingAddress = billingAddress;
    }

    public PerhapsLoaded<Address> getDeliveryAddress()
    {
        return deliveryAddress;
    }

    public void setDeliveryAddress(PerhapsLoaded<Address> deliveryAddress)
    {
        this.deliveryAddress = deliveryAddress;
    }
}
