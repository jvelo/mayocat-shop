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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.StrategyPriceCalculator;
import org.mayocat.shop.shipping.model.Carrier;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class FlatStrategyPriceCalculatorTest
{
    @Rule
    public final MockitoComponentMockingRule<StrategyPriceCalculator> componentManager =
            new MockitoComponentMockingRule(FlatStrategyPriceCalculator.class);

    @Test
    public void testFlatStrategyPriceCalculation() throws ComponentLookupException
    {
        Carrier carrier = new Carrier();
        carrier.setPerShipping(BigDecimal.TEN);
        carrier.setPerItem(BigDecimal.ONE);

        Map<Purchasable, Long> items = Maps.newHashMap();

        Product product1 = new Product();
        product1.setPrice(BigDecimal.valueOf(25));

        Product product2 = new Product();
        product2.setPrice(BigDecimal.valueOf((100)));

        items.put(product1, 3l);

        BigDecimal price = this.componentManager.getComponentUnderTest().getPrice(carrier, items);

        Assert.assertEquals(BigDecimal.valueOf(13), price);

        items.put(product1, 4l);
        price = this.componentManager.getComponentUnderTest().getPrice(carrier, items);
        Assert.assertEquals(BigDecimal.valueOf(14), price);

        items.put(product2, 3l);
        price = this.componentManager.getComponentUnderTest().getPrice(carrier, items);
        Assert.assertEquals(BigDecimal.valueOf(17), price);
    }

    @Test
    public void testFlatStrategyPriceCalculationWhenCartIsEmpty() throws ComponentLookupException
    {
        Carrier carrier = new Carrier();
        carrier.setPerShipping(BigDecimal.TEN);
        carrier.setPerItem(BigDecimal.ONE);

        Map<Purchasable, Long> items = Maps.newHashMap();

        BigDecimal price = this.componentManager.getComponentUnderTest().getPrice(carrier, items);

        Assert.assertEquals(BigDecimal.ZERO, price);
    }
}
