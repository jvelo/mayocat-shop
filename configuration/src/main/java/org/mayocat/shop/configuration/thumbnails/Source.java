package org.mayocat.shop.configuration.thumbnails;

/**
* @version $Id$
*/
public enum Source
{
    PLATFORM ("platform"),
    THEME ("name");

    private final String name;
    Source(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
