package org.mayocat.configuration.general;

import javax.validation.Valid;

import org.mayocat.configuration.ExposedSettings;
import org.mayocat.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class GeneralSettings implements ExposedSettings
{
    @Valid
    @JsonProperty
    private LocalesSettings locales = new LocalesSettings();

    @Valid
    @JsonProperty
    private TimeSettings time = new TimeSettings();

    public LocalesSettings getLocales()
    {
        return locales;
    }

    public TimeSettings getTime()
    {
        return time;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @JsonIgnore
    public String getKey()
    {
        return "general";
    }

}
