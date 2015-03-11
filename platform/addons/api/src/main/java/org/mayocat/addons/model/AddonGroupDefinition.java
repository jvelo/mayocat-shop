/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons.model;

import java.util.List;
import java.util.Map;

import org.mayocat.jackson.OptionalStringListDeserializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class AddonGroupDefinition
{
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String text;

    private Map<String, AddonFieldDefinition> fields;

    private Map<String, Object> properties = Maps.newHashMap();

    @JsonDeserialize(using = OptionalStringListDeserializer.class)
    @JsonProperty("for")
    private Optional<List<String>> entities = Optional.absent();

    @JsonDeserialize(using = OptionalStringListDeserializer.class)
    private Optional<List<String>> models = Optional.absent();

    private boolean sequence;

    public Optional<List<String>> getEntities()
    {
        return entities;
    }

    public Optional<List<String>> getModels()
    {
        return models;
    }

    public String getName()
    {
        return name;
    }

    public String getText()
    {
        return text;
    }

    public Map<String, AddonFieldDefinition> getFields()
    {
        return fields;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public boolean isSequence()
    {
        return sequence;
    }
}
