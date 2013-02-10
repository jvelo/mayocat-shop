package org.mayocat.shop.configuration.general;

import javax.validation.Valid;

import org.mayocat.shop.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class GeneralConfiguration
{
    @Valid
    @JsonProperty
    private Configurable<String> name = new Configurable<String>("", true);

    @Valid
    @JsonProperty
    private LocalesConfiguration locales = new LocalesConfiguration();

    public LocalesConfiguration getLocales()
    {
        return locales;
    }
}
