/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.general;

import javax.validation.Valid;

import org.mayocat.configuration.ExposedSettings;
import org.mayocat.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class GeneralSettings implements ExposedSettings
{
    @Valid
    @JsonProperty
    private LocalesSettings locales = new LocalesSettings();

    @Valid
    @JsonProperty
    private TimeSettings time = new TimeSettings();

    @Valid
    @JsonProperty
    private String notificationsEmail = "Mayocat Shop Notifications<no-reply@mayocat.org>";

    public LocalesSettings getLocales()
    {
        return locales;
    }

    public TimeSettings getTime()
    {
        return time;
    }

    public String getNotificationsEmail()
    {
        return notificationsEmail;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @JsonIgnore
    public String getKey()
    {
        return "general";
    }

}
