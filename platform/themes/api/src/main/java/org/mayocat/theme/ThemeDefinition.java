/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.theme;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import org.hibernate.validator.constraints.NotBlank;
import org.mayocat.addons.model.AddonGroupDefinition;
import org.mayocat.configuration.images.ImageFormatDefinition;

/**
 * @version $Id$
 */
public class ThemeDefinition
{
    @NotBlank
    @JsonProperty
    private String name = "";

    @JsonProperty
    private String description = "";

    @JsonProperty
    private Map<String, ImageFormatDefinition> images = Maps.newHashMap();

    @JsonProperty
    private Map<String, Model> models = Maps.newLinkedHashMap();

    @JsonProperty
    private Map<String, AddonGroupDefinition> addons = Collections.emptyMap();

    @JsonProperty
    private Map<String, PaginationDefinition> pagination = Collections.emptyMap();

    @JsonProperty
    private Map<String, TypeDefinition> productTypes = Maps.newHashMap();

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Map<String, ImageFormatDefinition> getImageFormats()
    {
        return images;
    }

    public Map<String, Model> getModels()
    {
        return models;
    }

    public Map<String, AddonGroupDefinition> getAddons()
    {
        return this.addons;
    }

    @JsonIgnore
    public Map<String, PaginationDefinition> getPaginationDefinitions()
    {
        return pagination;
    }

    public PaginationDefinition getPaginationDefinition(String key)
    {
        return pagination.containsKey(key) ? pagination.get(key) : new PaginationDefinition();
    }

    public Map<String, TypeDefinition> getProductTypes()
    {
        return productTypes;
    }
}
