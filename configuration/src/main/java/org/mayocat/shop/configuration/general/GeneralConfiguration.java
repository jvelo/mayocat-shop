package org.mayocat.shop.configuration.general;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class GeneralConfiguration
{
    @Valid
    @JsonProperty
    private LocalesConfiguration locales;

    public LocalesConfiguration getLocales()
    {
        return locales;
    }
}
