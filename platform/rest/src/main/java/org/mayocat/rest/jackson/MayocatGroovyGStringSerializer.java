package org.mayocat.rest.jackson;

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import groovy.lang.GString;

/**
 * Serializer that transforms a Groovy GString into a regalur Java String.
 *
 * /`Y`\
 *
 * @version $Id$
 */
public class MayocatGroovyGStringSerializer extends StdSerializer<GString>
{
    protected MayocatGroovyGStringSerializer()
    {
        super(GString.class);
    }

    @Override
    public void serialize(GString gString, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException, JsonGenerationException
    {
        // IETF BCP 47
        jsonGenerator.writeString(gString.toString());
    }
}
