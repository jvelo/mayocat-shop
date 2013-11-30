/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.jackson;

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @version $Id$
 */
public class LocaleBCP47LanguageTagSerializer extends StdSerializer<Locale>
{
    protected LocaleBCP47LanguageTagSerializer()
    {
        super(Locale.class);
    }

    @Override
    public void serialize(Locale locale, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException, JsonGenerationException
    {
        // IETF BCP 47
        jsonGenerator.writeString(locale.toLanguageTag());
    }
}
