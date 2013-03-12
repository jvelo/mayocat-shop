package org.mayocat.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @version $Id$
 */
public enum AddonSource
{
    PLATFORM,
    THEME;

    @JsonValue
    public String toJson()
    {
        return name().toLowerCase();
    }

    @JsonCreator
    public static AddonSource fromJson(String text)
    {
        return valueOf(text.toUpperCase());
    }

}
