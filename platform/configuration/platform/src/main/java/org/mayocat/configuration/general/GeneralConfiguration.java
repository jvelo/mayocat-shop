package org.mayocat.configuration.general;

import javax.validation.Valid;

import org.mayocat.configuration.Configurable;

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
    private Configurable<String> tagline = new Configurable<String>("", true);

    @Valid
    @JsonProperty
    private LocalesConfiguration locales = new LocalesConfiguration();

    public LocalesConfiguration getLocales()
    {
        return locales;
    }

    public Configurable<String> getName()
    {
        return name;
    }

    public Configurable<String> getTagline()
    {
        return tagline;
    }
}
