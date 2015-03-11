/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class OrderItem
{
    private UUID id;

    private UUID orderId;

    private UUID purchasableId;

    private String type;

    private String title;

    private String merchant;

    private Long quantity;

    private BigDecimal unitPrice;

    private BigDecimal itemTotal;

    private BigDecimal vatRate;

    private Map<String, Object> data = Maps.newHashMap();

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public UUID getOrderId()
    {
        return orderId;
    }

    public void setOrderId(UUID orderId)
    {
        this.orderId = orderId;
    }

    public UUID getPurchasableId()
    {
        return purchasableId;
    }

    public void setPurchasableId(UUID purchasableId)
    {
        this.purchasableId = purchasableId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getMerchant()
    {
        return merchant;
    }

    public void setMerchant(String merchant)
    {
        this.merchant = merchant;
    }

    public Long getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Long quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getItemTotal()
    {
        return itemTotal;
    }

    public void setItemTotal(BigDecimal itemTotal)
    {
        this.itemTotal = itemTotal;
    }

    public BigDecimal getVatRate()
    {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate)
    {
        this.vatRate = vatRate;
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public void addData(String key, Object value)
    {
        this.data.put(key, value);
    }

    public void addData(Map<String, Object> data)
    {
        this.data.putAll(data);
    }
}
