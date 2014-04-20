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
import org.mayocat.shop.front.context.ImageContext;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @version $Id$
 */
public class CartItemContext
{
    private String title;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String variant;

    private String description;

    private Long quantity;

    private String type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String slug;

    private UUID id;

    private PriceRepresentation unitPrice;

    private PriceRepresentation itemTotal;

    private ImageContext featuredImage;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getVariant()
    {
        return variant;
    }

    public void setVariant(String variant)
    {
        this.variant = variant;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Long getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Long quantity)
    {
        this.quantity = quantity;
    }

    public PriceRepresentation getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(PriceRepresentation unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public PriceRepresentation getItemTotal()
    {
        return itemTotal;
    }

    public void setItemTotal(PriceRepresentation itemTotal)
    {
        this.itemTotal = itemTotal;
    }

    public ImageContext getFeaturedImage()
    {
        return featuredImage;
    }

    public void setFeaturedImage(ImageContext featuredImage)
    {
        this.featuredImage = featuredImage;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }
}
