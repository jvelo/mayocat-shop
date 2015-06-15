package org.mayocat.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import java.io.IOException;

/**
 * @version $Id$
 */
public class OptionalStringDeserializer extends JsonDeserializer<Optional<String>>
{
    @Override
    public Optional<String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        if (Strings.isNullOrEmpty(jsonParser.getValueAsString())) {
            return Optional.absent();
        }
        return Optional.of(jsonParser.getValueAsString());
    }
}
