package org.mayocat.shop.catalog.api.representations;

import org.mayocat.model.Addon;

/**
 * @version $Id$
 */
public class AddonRepresentation
{
    private Object value;

    private String type;

    private String source;

    private String hint;

    private String name;

    public AddonRepresentation(Addon addon)
    {
        this.value = addon.getValue();
        this.type = addon.getType().toJson();
        this.hint = addon.getHint();
        this.source = addon.getSource().toJson();
        this.name = addon.getName();
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getHint()
    {
        return hint;
    }

    public void setHint(String hint)
    {
        this.hint = hint;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
