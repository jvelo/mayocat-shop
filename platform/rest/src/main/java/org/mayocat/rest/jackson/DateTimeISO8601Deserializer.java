/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.jackson;

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

import com.google.common.base.Strings;

public class DateTimeISO8601Deserializer extends JsonDeserializer<DateTime>
{
    @Override
    public DateTime deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException,
            JsonProcessingException
    {
        String str = parser.getText().trim();
        if (Strings.isNullOrEmpty(str)) {
            return null;
        }

        ConfigurationService configurationService = Utils.getComponent(ConfigurationService.class);
        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class);
        return new DateTime(str, DateTimeZone.forTimeZone(settings.getTime().getTimeZone().getValue()));
    }
}
