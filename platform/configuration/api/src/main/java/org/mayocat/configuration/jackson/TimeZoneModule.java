/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.jackson;

import java.io.IOException;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers;

/**
 * @version $Id$
 */
public class TimeZoneModule extends Module
{
    private static class TimeZoneDeserializer extends JsonDeserializer<TimeZone>
    {
        @Override
        public TimeZone deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException, JsonProcessingException
        {
            return TimeZone.getTimeZone(jsonParser.getText());
        }
    }

    private static class TimeZoneDeserializers extends Deserializers.Base
    {
        @Override
        public JsonDeserializer<?> findBeanDeserializer(JavaType type,
                DeserializationConfig config,
                BeanDescription beanDesc) throws JsonMappingException
        {
            if (TimeZone.class.isAssignableFrom(type.getRawClass())) {
                return new TimeZoneDeserializer();
            }
            return super.findBeanDeserializer(type, config, beanDesc);
        }
    }

    @Override
    public String getModuleName()
    {
        return "MayocatTimeZoneModule";
    }

    @Override
    public Version version()
    {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext setupContext)
    {
        setupContext.addDeserializers(new TimeZoneDeserializers());
    }
}
