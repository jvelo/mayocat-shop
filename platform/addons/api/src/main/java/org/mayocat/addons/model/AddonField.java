package org.mayocat.addons.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private String editor;

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

    public String getEditor()
    {
        return editor;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }
}
