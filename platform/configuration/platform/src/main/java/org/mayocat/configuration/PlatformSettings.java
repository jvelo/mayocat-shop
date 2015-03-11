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
import org.mayocat.theme.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class PlatformSettings
{
    @JsonProperty
    private Map<String, Model> models = Maps.newLinkedHashMap();

    @Valid
    @JsonProperty
    private Map<String, ImageFormatDefinition> images = Maps.newHashMap();

    @Valid
    @JsonProperty
    private Map<String, AddonGroupDefinition> addons = Collections.emptyMap();

    public Map<String, ImageFormatDefinition> getImages()
    {
        return images;
    }

    public Map<String, AddonGroupDefinition> getAddons()
    {
        return this.addons;
    }

    public Map<String, Model> getModels()
    {
        return models;
    }
}
