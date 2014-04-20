/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.jackson;

import java.util.Locale;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @version $Id$
 */
public class MayocatLocaleBCP47LanguageTagModule extends SimpleModule
{
    public MayocatLocaleBCP47LanguageTagModule()
    {
        super(Version.unknownVersion());
        addDeserializer(Locale.class, new LocaleBCP47LanguageTagDeserializer());
        addSerializer(Locale.class, new LocaleBCP47LanguageTagSerializer());
    }
}
