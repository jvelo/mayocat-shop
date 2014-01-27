/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.theme;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

/**
 * Holds the definition of variants for an entity
 *
 * @version $Id$
 */
public class FeatureDefinition
{
    @JsonProperty
    @NotBlank
    private String name;

    @JsonProperty
    private Map<String, String> keys = Maps.newHashMap();

    public String getName()
    {
        return name;
    }

    public Map<String, String> getKeys()
    {
        return keys;
    }
}
