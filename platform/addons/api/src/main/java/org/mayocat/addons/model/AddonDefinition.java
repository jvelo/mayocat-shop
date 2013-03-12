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
    private String name;

    @JsonProperty("display")
    private String displayName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String placeholder;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String template;

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

    public String getDisplayName()
    {
        return Strings.isNullOrEmpty(this.displayName) ? this.getName() : this.displayName;
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
