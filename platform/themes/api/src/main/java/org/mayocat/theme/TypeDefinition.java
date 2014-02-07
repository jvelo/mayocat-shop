/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.theme;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Represents the definition of a product type.
 *
 * @version $Id$
 */
public class TypeDefinition
{
    private String name;

    private List<String> variantProperties = Arrays.asList("stock", "price");

    private Map<String, FeatureDefinition> features = Maps.newHashMap();

    public String getName()
    {
        return name;
    }

    public Map<String, FeatureDefinition> getFeatures()
    {
        return features;
    }

    public List<String> getVariantProperties()
    {
        return variantProperties;
    }
}
