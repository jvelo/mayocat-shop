/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mayocat.model.Identifiable;
import org.mayocat.shop.shipping.Strategy;

/**
 * @version $Id$
 */
public class Carrier implements Identifiable
{
    private UUID id;

    private UUID tenantId;

    private List<String> destinations;

    private Strategy strategy;

    private String title;

    private String description;

    private Integer minimumDays;

    private Integer maximumDays;

    private BigDecimal perShipping;

    private BigDecimal perItem;

    private BigDecimal perAdditionalUnit;

    private List<CarrierRule> rules = new ArrayList<CarrierRule>();

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public UUID getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(UUID tenantId)
    {
        this.tenantId = tenantId;
    }

    public List<String> getDestinations()
    {
        return destinations;
    }

    public void setDestinations(List<String> destinations)
    {
        this.destinations = destinations;
    }

    public Strategy getStrategy()
    {
        return strategy;
    }

    public void setStrategy(Strategy strategy)
    {
        this.strategy = strategy;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Integer getMinimumDays()
    {
        return minimumDays;
    }

    public void setMinimumDays(Integer minimumDays)
    {
        this.minimumDays = minimumDays;
    }

    public Integer getMaximumDays()
    {
        return maximumDays;
    }

    public void setMaximumDays(Integer maximumDays)
    {
        this.maximumDays = maximumDays;
    }

    public List<CarrierRule> getRules()
    {
        return rules;
    }

    public void addRule(CarrierRule rule)
    {
        this.rules.add(rule);
    }

    public BigDecimal getPerShipping()
    {
        return perShipping;
    }

    public void setPerShipping(BigDecimal perShipping)
    {
        this.perShipping = perShipping;
    }

    public BigDecimal getPerItem()
    {
        return perItem;
    }

    public void setPerItem(BigDecimal perItem)
    {
        this.perItem = perItem;
    }

    public BigDecimal getPerAdditionalUnit()
    {
        return perAdditionalUnit;
    }

    public void setPerAdditionalUnit(BigDecimal perAdditionalUnit)
    {
        this.perAdditionalUnit = perAdditionalUnit;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Carrier other = (Carrier) obj;

        if (this.getId() != null && other.getId() != null) {
            return this.getId().equals(other.getId());
        }

        return false;
    }
}
