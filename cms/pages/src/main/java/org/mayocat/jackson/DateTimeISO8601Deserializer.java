package org.mayocat.jackson;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.mayocat.util.Utils;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateTimeISO8601Deserializer extends JsonDeserializer<DateTime>
{
    @Override
    public DateTime deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException,
            JsonProcessingException
    {
        String str = parser.getText().trim();
        if (str.length() == 0) {
            return null;
        }

        ConfigurationService cs = Utils.getComponent(ConfigurationService.class);
        GeneralSettings settings = cs.getSettings(GeneralSettings.class);
        return new DateTime(str, DateTimeZone.forTimeZone(settings.getTime().getTimeZone().getValue()));
    }
}
