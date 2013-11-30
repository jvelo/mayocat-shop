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
import org.mayocat.shop.shipping.model.CarrierRule;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class PriceStrategyPriceCalculatorTest
{
    @Rule
    public final MockitoComponentMockingRule<StrategyPriceCalculator> componentManager =
            new MockitoComponentMockingRule(PriceStrategyPriceCalculator.class);

    @Test
    public void testPriceStrategyPriceCalculation() throws ComponentLookupException
    {
        Carrier carrier = new Carrier();
        CarrierRule rule1 = new CarrierRule();
        CarrierRule rule2 = new CarrierRule();
        CarrierRule rule3 = new CarrierRule();

        rule1.setUpToValue(BigDecimal.valueOf(100));
        rule1.setPrice(BigDecimal.valueOf(50));

        rule2.setUpToValue(BigDecimal.valueOf(200));
        rule2.setPrice(BigDecimal.valueOf(25));

        rule3.setUpToValue(BigDecimal.valueOf(300));
        rule3.setPrice(BigDecimal.valueOf(5));

        carrier.addRule(rule1);
        carrier.addRule(rule2);
        carrier.addRule(rule3);

        Map<Purchasable, Long> items = Maps.newHashMap();

        Product product1 = new Product();
        product1.setPrice(BigDecimal.valueOf(25));

        Product product2 = new Product();
        product2.setPrice(BigDecimal.valueOf((100)));

        items.put(product1, 3l);

        BigDecimal price = this.componentManager.getComponentUnderTest().getPrice(carrier, items);

        Assert.assertEquals(BigDecimal.valueOf(50), price);

        items.put(product1, 4l);
        price = this.componentManager.getComponentUnderTest().getPrice(carrier, items);
        Assert.assertEquals(BigDecimal.valueOf(50), price);

        items.put(product1, 5l); // 125
        price = this.componentManager.getComponentUnderTest().getPrice(carrier, items);
        Assert.assertEquals(BigDecimal.valueOf(25), price);

        items.put(product2, 1l); // 225
        price = this.componentManager.getComponentUnderTest().getPrice(carrier, items);
        Assert.assertEquals(BigDecimal.valueOf(5), price);
    }
}
