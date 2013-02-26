package org.mayocat.configuration.thumbnails;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class ThumbnailDefinition
{
    @JsonProperty
    private String name;

    @JsonProperty
    private Dimensions dimensions;

    public ThumbnailDefinition(String name, Dimensions dimensions)
    {
        this.name = name;
        this.dimensions = dimensions;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Dimensions getDimensions()
    {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions)
    {
        this.dimensions = dimensions;
    }
}
