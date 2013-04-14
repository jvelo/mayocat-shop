package org.mayocat.jackson;

import org.junit.Assert;
import org.junit.Test;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.mayocat.cms.jackson.DateTimeISO8601Serializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class DateTimeISO8601SerializerTest
{
    private ObjectMapper mapper = jodaMapper();

    private final ObjectMapper jodaMapper()
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null));
        testModule.addSerializer(new DateTimeISO8601Serializer());
        mapper.registerModule(testModule);
        return mapper;
    }


    @Test
    public void testSerializeWithProperFormat() throws Exception
    {
        DateTime dt = new DateTime("2013-04-09T09:06:17.000+02:00", DateTimeZone.forID("Europe/Paris"));

        // Format pattern is valid and formatted date is an ISO time string without milliseconds
        Assert.assertEquals(quote("2013-04-09T09:06:17+02:00"), mapper.writeValueAsString(dt));
    }

    private String quote(String str) {
        return '"'+str+'"';
    }

}
