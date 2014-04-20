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

import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.Purchasable;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("weight")
public class WeightStrategyPriceCalculator extends AbstractValueBasedStrategyPriceCalculator
{
    @Override
    protected BigDecimal getValue(Map<Purchasable, Long> items)
    {
        BigDecimal weight = BigDecimal.ZERO;
        for (Purchasable purchasable : items.keySet()) {
            try {
                weight = weight.add(purchasable.getActualWeight().isPresent() ?
                        purchasable.getActualWeight().get().multiply(BigDecimal.valueOf(items.get(purchasable))) :
                        BigDecimal.ZERO);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException(
                        "Cannot calculate weight-based shipping price for something else than a product", e);
            }
        }
        return weight;
    }
}
