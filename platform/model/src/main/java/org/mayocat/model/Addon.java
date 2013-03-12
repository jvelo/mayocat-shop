package org.mayocat.model;

/**
 * @version $Id$
 */
public class Addon<T>
{
    private T value;

    private AddonSource source;

    private String hint;

    private String name;

    private AddonFieldType type;

    public T getValue()
    {
        return value;
    }

    public AddonSource getSource()
    {
        return source;
    }

    public String getHint()
    {
        return hint;
    }

    public String getName()
    {
        return name;
    }

    public AddonFieldType getType()
    {
        return type;
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    public void setSource(AddonSource source)
    {
        this.source = source;
    }

    public void setHint(String hint)
    {
        this.hint = hint;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setType(AddonFieldType type)
    {
        this.type = type;
    }
}
