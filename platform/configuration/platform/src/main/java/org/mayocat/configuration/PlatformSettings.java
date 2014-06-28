/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration;

import java.util.Collections;
import java.util.Map;

import javax.validation.Valid;

import org.mayocat.addons.model.AddonGroupDefinition;
import org.mayocat.configuration.images.ImageFormatDefinition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class PlatformSettings
{
    @Valid
    @JsonProperty
    private Map<String, ImageFormatDefinition> thumbnails = Maps.newHashMap();

    @Valid
    @JsonProperty
    private Map<String, AddonGroupDefinition> addons = Collections.emptyMap();

    public Map<String, ImageFormatDefinition> getThumbnails()
    {
        return thumbnails;
    }

    public Map<String, AddonGroupDefinition> getAddons()
    {
        return this.addons;
    }
}
