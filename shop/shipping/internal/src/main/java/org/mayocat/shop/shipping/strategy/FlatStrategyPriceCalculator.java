/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping.strategy;

import java.math.BigDecimal;
import java.util.Map;

import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.StrategyPriceCalculator;
import org.mayocat.shop.shipping.model.Carrier;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("flat")
public class FlatStrategyPriceCalculator implements StrategyPriceCalculator
{
    @Override
    public BigDecimal getPrice(Carrier carrier, Map<Purchasable, Long> items)
    {
        BigDecimal price = carrier.getPerShipping() != null ? carrier.getPerShipping() : BigDecimal.ZERO;
        Long numberOfItems = 0l;
        for (Long number : items.values()) {
            numberOfItems += number;
        }

        if (numberOfItems == 0) {
            // If the cart is empty, there's no shipping to be paid
            return BigDecimal.ZERO;
        }

        BigDecimal perItem = carrier.getPerItem() != null ? carrier.getPerItem() : BigDecimal.ZERO;
        price = price.add(perItem.multiply(BigDecimal.valueOf(numberOfItems)));
        return price;
    }
}
