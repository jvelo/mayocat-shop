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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DateTimeISO8601Serializer extends StdSerializer<DateTime>
{
    public DateTimeISO8601Serializer()
    {
        super(DateTime.class);
    }

    @Override
    public void serialize(DateTime value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException
    {
        // ISO 8601 without milliseconds
        jsonGenerator.writeString(value.toString("yyyy-MM-dd'T'HH:mm:ssZZ"));
    }
}
