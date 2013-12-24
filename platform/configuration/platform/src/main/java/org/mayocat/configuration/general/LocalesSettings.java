/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.general;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.mayocat.configuration.Configurable;
import org.mayocat.jackson.OptionalStringListDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @version $Id$
 */
public class LocalesSettings
{
    @Valid
    @JsonProperty("main")
    private Configurable<Locale> mainLocale = new Configurable(Locale.ENGLISH);

    @Valid
    @JsonProperty("others")
    private Configurable<List<Locale>> otherLocales = new Configurable<>(Collections.<Locale>emptyList());

    public Configurable<Locale> getMainLocale()
    {
        return mainLocale;
    }

    public Configurable<List<Locale>> getOtherLocales()
    {
        return otherLocales;
    }
}
