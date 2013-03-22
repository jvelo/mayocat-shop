package org.mayocat.configuration.general;

import java.util.TimeZone;

import javax.validation.Valid;

import org.mayocat.base.ExposedSettings;
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
    private Configurable<String> name = new Configurable<String>("", true);

    @Valid
    @JsonProperty
    private Configurable<String> tagline = new Configurable<String>("", true);

    @Valid
    @JsonProperty
    private Configurable<TimeZone> timeZone = new Configurable<TimeZone>(TimeZone.getTimeZone("GMT"), true);

    @Valid
    @JsonProperty
    private LocalesSettings locales = new LocalesSettings();

    public LocalesSettings getLocales()
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

    public Configurable<TimeZone> getTimeZone()
    {
        return timeZone;
    }

    @Override
    @JsonIgnore
    public String getKey()
    {
        return "general";
    }
}
