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