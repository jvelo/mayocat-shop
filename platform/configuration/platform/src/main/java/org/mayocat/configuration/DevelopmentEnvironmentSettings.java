/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Settings for the development environment
 *
 * @version $Id$
 */
public class DevelopmentEnvironmentSettings
{
    @Valid
    @JsonProperty
    private boolean enabled = false;

    @Valid
    @JsonProperty
    private boolean logDatabaseRequests = false;

    public boolean isEnabled()
    {
        return enabled;
    }

    public boolean isLogDatabaseRequests()
    {
        return logDatabaseRequests;
    }
}
