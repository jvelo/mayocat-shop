/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.theme;

import java.util.List;

import org.mayocat.jackson.OptionalStringListDeserializer;
import org.mayocat.jackson.PasswordSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

/**
 * A theme model is a configurable layout template that can be applied to entities. For example, a theme can declare
 * several different models for different kinds of products.
 *
 * @version $Id$
 */
public class Model
{
    private String file;

    private String name;

    @JsonDeserialize(using = OptionalStringListDeserializer.class)
    @JsonProperty("for")
    private Optional<List<String>> entities = Optional.absent();

    public String getFile()
    {
        return file;
    }

    public String getName()
    {
        return name;
    }

    public Optional<List<String>> getEntities()
    {
        return entities;
    }
}