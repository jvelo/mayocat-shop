package org.mayocat.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class PasswordSerializer extends JsonSerializer<String>
{
    @Override
    public void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException
    {
        jgen.writeString("********");
    }
}
