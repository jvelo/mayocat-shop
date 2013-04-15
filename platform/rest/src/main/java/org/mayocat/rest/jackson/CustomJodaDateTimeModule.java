package org.mayocat.rest.jackson;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class CustomJodaDateTimeModule extends SimpleModule
{
    public CustomJodaDateTimeModule()
    {
        super(Version.unknownVersion());

        addDeserializer(DateTime.class, new DateTimeISO8601Deserializer());

        addSerializer(DateTime.class, new DateTimeISO8601Serializer());
    }
}
