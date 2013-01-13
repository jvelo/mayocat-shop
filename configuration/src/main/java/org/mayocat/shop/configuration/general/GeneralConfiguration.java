package org.mayocat.shop.configuration.general;

import javax.validation.Valid;

import org.mayocat.shop.configuration.Configuration;
import org.mayocat.shop.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class GeneralConfiguration implements Configuration
{
    @Valid
    @JsonProperty
    private Configurable<String> name = new Configurable<String>("", true);

    @Valid
    @JsonProperty
    private LocalesConfiguration locales;

    public LocalesConfiguration getLocales()
    {
        return locales;
    }
}
