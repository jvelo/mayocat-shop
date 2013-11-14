package org.mayocat.configuration.images;

/**
 * @version $Id$
 */
public enum Source
{
    PLATFORM("platform"),
    THEME("theme");

    private final String name;

    Source(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
