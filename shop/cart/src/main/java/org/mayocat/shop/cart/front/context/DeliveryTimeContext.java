/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
