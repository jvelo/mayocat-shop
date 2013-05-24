package org.mayocat.addons.model;

import java.util.List;
import java.util.Map;

import org.mayocat.jackson.OptionalStringListDeserializer;
import org.mayocat.model.AddonFieldType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class AddonField
{
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String placeholder;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String template;

    private String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String displayer;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> properties = Maps.newHashMap();

    public String getType()
    {
        return type;
    }

    public String getName()
    {
        return this.name;
    }

    public String getPlaceholder()
    {
        return placeholder;
    }

    public String getTemplate()
    {
        return template;
    }

    public String getDisplayer()
    {
        return displayer;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }
}
