/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.configuration.jackson;

import java.util.Currency;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @version $Id$
 */
public class MoneyTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception
    {
        mapper.registerModule(new MoneyModule());
    }

    @Test
    public void testCurrencyDeserialization() throws Exception
    {
        Assert.assertEquals(Currency.getInstance("EUR"), mapper.readValue("\"EUR\"", Currency.class));
    }

}
