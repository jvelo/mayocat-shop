package org.mayocat.shop.shipping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @version $Id$
 */
public enum Strategy
{
    NONE,
    WEIGHT,
    PRICE,
    FLAT;

    @JsonValue
    public String toJson()
    {
        return name().toLowerCase();
    }

    @JsonCreator
    public static Strategy fromJson(String text)
    {
        return valueOf(text.toUpperCase());
    }

}
