/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Settings for the localization filter. See {@link org.mayocat.localization.RequestLocalizationFilter}.
 *
 * @version $Id$
 */
public class LocalizationFilterSettings
{
    @JsonProperty
    private List<String> excludePaths = Arrays.asList("/api");

    public List<String> getExcludePaths()
    {
        return excludePaths;
    }
}
