package org.mayocat.theme;

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

    private Map<String, FeatureDefinition> features = Maps.newHashMap();

    public String getName()
    {
        return name;
    }

    public Map<String, FeatureDefinition> getFeatures()
    {
        return features;
    }
}
