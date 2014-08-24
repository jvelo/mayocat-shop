/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.front.representation;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.mayocat.shop.catalog.front.representation.PriceRepresentation;

/**
 * @version $Id$
 */
public class PriceRepresentationTest
{
    @Test
    public void testPriceCompactFormatWhenAmountHasNoDecimal()
    {
        PriceRepresentation priceRepresentation =
                new PriceRepresentation(BigDecimal.TEN, Currency.getInstance("EUR"), Locale.FRANCE);
        Assert.assertEquals("10", priceRepresentation.getAmountCompact());
    }

    @Test
    public void testPriceCompactFormatWhenAmountHasDecimals()
    {
        PriceRepresentation priceRepresentation =
                new PriceRepresentation(BigDecimal.valueOf(3.50), Currency.getInstance("USD"), Locale.US);
        Assert.assertEquals("I hate it when the Loch Ness Monster asks me for $3.50", "3.50",
                priceRepresentation.getAmountCompact());

        priceRepresentation =
                new PriceRepresentation(BigDecimal.valueOf(3.145), Currency.getInstance("AOA"), Locale.FRANCE);
        Assert.assertEquals("3,1", priceRepresentation.getAmountCompact());
    }
}
