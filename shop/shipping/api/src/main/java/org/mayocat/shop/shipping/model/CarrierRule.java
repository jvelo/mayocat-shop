package org.mayocat.shop.shipping.model;

import java.math.BigDecimal;

/**
 * @version $Id$
 */
public class CarrierRule
{
    private BigDecimal upToValue;

    private BigDecimal price;

    public BigDecimal getUpToValue()
    {
        return upToValue;
    }

    public void setUpToValue(BigDecimal upToValue)
    {
        this.upToValue = upToValue;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }
}
