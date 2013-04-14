package org.mayocat.configuration.json;

import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mayocat.configuration.jackson.TimeZoneModule;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @version $Id$
 */
public class TimeZoneModuleTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception
    {
        mapper.registerModule(new TimeZoneModule());
    }

    @Test
    public void testTimeZoneDeserialization() throws Exception
    {
        Assert.assertEquals(TimeZone.getTimeZone("Europe/Paris"), mapper.readValue("\"Europe/Paris\"", TimeZone.class));
    }
}
