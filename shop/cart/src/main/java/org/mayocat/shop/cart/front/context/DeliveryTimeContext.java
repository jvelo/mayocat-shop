package org.mayocat.shop.cart.front.context;

/**
 * @version $Id$
 */
public class DeliveryTimeContext
{
    private Integer minimumDays;

    private Integer maximumDays;

    public DeliveryTimeContext(Integer minimumDays, Integer maximumDays)
    {
        this.minimumDays = minimumDays;
        this.maximumDays = maximumDays;
    }

    public Integer getMinimumDays()
    {
        return minimumDays;
    }

    public Integer getMaximumDays()
    {
        return maximumDays;
    }
}
