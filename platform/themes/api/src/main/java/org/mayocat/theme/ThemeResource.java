package org.mayocat.theme;

import java.nio.file.Path;

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

    private Path path;

    public ThemeResource(Type type, Path path)
    {
        this.type = type;
        this.path = path;
    }

    public Path getPath()
    {
        return this.path;
    }

    public Type getType()
    {
        return this.type;
    }
}
