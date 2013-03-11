package org.mayocat.configuration;

import java.util.List;

import org.mayocat.jackson.OptionalStringListDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class AddonDefinition
{
    private String name;

    private AddonFieldType type;

    @JsonDeserialize(using = OptionalStringListDeserializer.class)
    @JsonProperty("for")
    private Optional<List<String>> entities = Optional.absent();

    public String getName()
    {
        return name;
    }

    public AddonFieldType getType()
    {
        return type;
    }

    public Optional<List<String>> getEntities()
    {
        return entities;
    }
}
