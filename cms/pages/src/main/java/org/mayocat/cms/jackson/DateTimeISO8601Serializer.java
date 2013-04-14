package org.mayocat.cms.jackson;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DateTimeISO8601Serializer extends StdSerializer<DateTime>
{
    public DateTimeISO8601Serializer() { super(DateTime.class); }

    @Override
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException
    {
        // ISO 8601 without milliseconds
        jgen.writeString(value.toString("yyyy-MM-dd'T'HH:mm:ssZZ"));
    }
}
