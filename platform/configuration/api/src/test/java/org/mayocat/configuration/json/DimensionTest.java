package org.mayocat.configuration.json;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mayocat.configuration.thumbnails.Dimensions;
import org.mayocat.configuration.thumbnails.jackson.ThumbnailsModule;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @version $Id$
 */
public class DimensionTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception
    {
        mapper.registerModule(new ThumbnailsModule());
    }

    @Test
    public void parseDimensionTest() throws Exception
    {
        Dimensions expected = new Dimensions(100, 300);
        Assert.assertEquals(expected, mapper.readValue("\"100x300\"", Dimensions.class));
        Assert.assertEquals(expected, mapper.readValue("\"100 x 300\"", Dimensions.class));
        Assert.assertEquals(expected, mapper.readValue("\" 100: 300\"", Dimensions.class));
        Assert.assertEquals(expected, mapper.readValue("\"100  :300   \"", Dimensions.class));
        Assert.assertEquals(expected, mapper.readValue("\"100 : 300\"", Dimensions.class));
    }

    @Test
    public void parseDimensionWithInvalidNumberTest() throws Exception
    {
        exception.expect(NumberFormatException.class);
        exception.expectMessage("For input string");
        mapper.readValue("\"100xx300\"", Dimensions.class);
    }

    @Test
    public void parseDimensionWithInvalidNumberTest2() throws Exception
    {
        exception.expect(NumberFormatException.class);
        exception.expectMessage("For input string");
        mapper.readValue("\"a100x300\"", Dimensions.class);
    }

    @Test
    public void parseDimensionWithNullValues() throws Exception
    {
        Assert.assertNull(mapper.readValue("\"x\"", Dimensions.class));
        Assert.assertNull(mapper.readValue("\":\"", Dimensions.class));
    }
}
