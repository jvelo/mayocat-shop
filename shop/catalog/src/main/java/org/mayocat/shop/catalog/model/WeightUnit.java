package org.mayocat.shop.catalog.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @version $Id$
 */
public enum WeightUnit
{
    KILOGRAM("kg"),
    GRAM("g"),
    OUNCE("oz"),
    POUND("lb");

    WeightUnit(String smybol)
    {
        this.symbol = smybol;
    }

    private String symbol;

    public String getSymbol()
    {
        return symbol;
    }

    @JsonValue
    public String toJson()
    {
        return getSymbol();
    }

    @JsonCreator
    public static WeightUnit fromJson(String text)
    {
        for (WeightUnit unit : values()) {
            if (unit.getSymbol().equalsIgnoreCase(text)) {
                return unit;
            }
        }
        return null;
    }
}
