package org.mayocat.addons.model;

import java.util.List;

import org.mayocat.jackson.OptionalStringListDeserializer;
import org.mayocat.model.AddonFieldType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * @version $Id$
 */
public class AddonDefinition
{
    private String key;

    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String placeholder;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String template;

    private String type;

    @JsonDeserialize(using = OptionalStringListDeserializer.class)
    @JsonProperty("for")
    private Optional<List<String>> entities = Optional.absent();

    public String getKey()
    {
        return key;
    }

    public String getType()
    {
        return type;
    }

    public Optional<List<String>> getEntities()
    {
        return entities;
    }

    public String getName()
    {
        return Strings.isNullOrEmpty(this.name) ? this.getKey() : this.name;
    }

    public String getPlaceholder()
    {
        return placeholder;
    }

    public String getTemplate()
    {
        return template;
    }
}
