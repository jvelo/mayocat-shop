/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.front.context;

import java.util.UUID;

import org.mayocat.shop.catalog.front.representation.PriceRepresentation;

/**
 * @version $Id$
 */
public class ShippingOptionContext
{
    private UUID id;

    private PriceRepresentation price;

    private String title;

    private boolean selected = false;

    private String destinations;

    private DeliveryTimeContext deliveryTime;

    public ShippingOptionContext(UUID id, String title, PriceRepresentation price)
    {
        this.price = price;
        this.title = title;
        this.id = id;
    }

    public PriceRepresentation getPrice()
    {
        return price;
    }

    public String getTitle()
    {
        return title;
    }

    public UUID getId()
    {
        return id;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public String getDestinations()
    {
        return destinations;
    }

    public void setDestinations(String destinations)
    {
        this.destinations = destinations;
    }

    public DeliveryTimeContext getDeliveryTime()
    {
        return deliveryTime;
    }

    public void setDeliveryTime(DeliveryTimeContext deliveryTime)
    {
        this.deliveryTime = deliveryTime;
    }
}
