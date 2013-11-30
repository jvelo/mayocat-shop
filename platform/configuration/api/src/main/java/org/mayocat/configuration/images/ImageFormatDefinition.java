/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.images;

import java.util.List;

import org.mayocat.jackson.OptionalStringListDeserializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class ImageFormatDefinition
{
    @JsonProperty
    private String name;

    @JsonProperty
    private Integer width;

    @JsonProperty
    private Integer height;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @JsonDeserialize(using = OptionalStringListDeserializer.class)
    @JsonProperty("for")
    private Optional<List<String>> entities = Optional.absent();

    public String getName()
    {
        return name;
    }

    public Integer getWidth()
    {
        return width;
    }

    public Integer getHeight()
    {
        return height;
    }

    public String getDescription()
    {
        return description;
    }

    public Optional<List<String>> getEntities()
    {
        return entities;
    }
}
