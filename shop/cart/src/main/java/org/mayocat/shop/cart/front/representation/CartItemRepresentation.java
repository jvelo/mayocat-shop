package org.mayocat.shop.cart.front.representation;

/**
 * @version $Id$
 */
public class CartItemRepresentation
{
    private String title;

    private String description;

    private Long quantity;

    private PriceRepresentation unitPrice;

    private PriceRepresentation total;

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

    public PriceRepresentation getTotal()
    {
        return total;
    }

    public void setTotal(PriceRepresentation total)
    {
        this.total = total;
    }
}
