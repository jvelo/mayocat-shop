package org.mayocat.shop.billing.model.stats;

import java.math.BigDecimal;

/**
 * @version $Id$
 */
public class TurnoverStatEntry
{
    private Integer numberOfOrders;

    private BigDecimal total;

    public Integer getNumberOfOrders()
    {
        return numberOfOrders;
    }

    public void setNumberOfOrders(Integer numberOfOrders)
    {
        this.numberOfOrders = numberOfOrders;
    }

    public BigDecimal getTotal()
    {
        return total;
    }

    public void setTotal(BigDecimal total)
    {
        this.total = total;
    }
}
