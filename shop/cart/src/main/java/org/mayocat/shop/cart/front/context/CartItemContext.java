package org.mayocat.shop.cart.front.context;

import org.mayocat.shop.catalog.front.representation.PriceRepresentation;
import org.mayocat.shop.front.context.ImageContext;

/**
 * @version $Id$
 */
public class CartItemContext
{
    private String title;

    private String description;

    private Long quantity;

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
}
