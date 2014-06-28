/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.theme;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mayocat.addons.model.AddonGroupDefinition;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Definition of variants
 *
 * @version $Id$
 */
public class VariantsDefinition
{
    @JsonProperty
    private Map<String, AddonGroupDefinition> addons = Collections.emptyMap();

    private List<String> properties = Arrays.asList("stock", "price");

    public Map<String, AddonGroupDefinition> getAddons()
    {
        return addons;
    }

    public List<String> getProperties()
    {
        return properties;
    }
}
