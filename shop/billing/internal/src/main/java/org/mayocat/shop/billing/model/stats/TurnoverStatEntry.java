/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
