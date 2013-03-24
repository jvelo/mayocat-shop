package org.mayocat.configuration.thumbnails;

import java.util.List;

import org.mayocat.jackson.OptionalStringListDeserializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class ThumbnailDefinition
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
