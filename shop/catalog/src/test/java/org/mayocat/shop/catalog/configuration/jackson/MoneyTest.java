package org.mayocat.shop.catalog.configuration.jackson;

import java.util.Currency;

import org.joda.money.CurrencyUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mayocat.configuration.thumbnails.Dimensions;

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
