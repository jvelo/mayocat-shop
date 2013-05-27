package org.mayocat.addons.model;

import java.util.List;
import java.util.Map;

import org.mayocat.jackson.OptionalStringListDeserializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class AddonGroup
{
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String text;

    private Map<String, AddonField> fields;

    private Map<String, Object> properties;

    @JsonDeserialize(using = OptionalStringListDeserializer.class)
    @JsonProperty("for")
    private Optional<List<String>> entities = Optional.absent();

    public Optional<List<String>> getEntities()
    {
        return entities;
    }

    public String getName()
    {
        return name;
    }

    public String getText()
    {
        return text;
    }

    public Map<String, AddonField> getFields()
    {
        return fields;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }
}
