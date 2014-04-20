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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class MayocatJodaModule extends SimpleModule
{
    public MayocatJodaModule()
    {
        super(Version.unknownVersion());

        addDeserializer(DateTime.class, new DateTimeISO8601Deserializer());

        addSerializer(DateTime.class, new DateTimeISO8601Serializer());
    }
}
