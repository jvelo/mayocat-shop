package org.mayocat.theme;

/**
 * @version $Id$
 */
public class ThemeResource
{
    public enum Type
    {
        CLASSPATH_RESOURCE,
        FILE
    }

    private Type type;

    private String path;

    public ThemeResource(Type type, String path)
    {
        this.type = type;
        this.path = path;
    }

    public String getPath()
    {
        return this.path;
    }

    public Type getType()
    {
        return this.type;
    }
}
