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

    @Valid
    @JsonProperty
    private String notificationsEmail = "Mayocat Shop Notifications<no-reply@mayocat.org>";

    public LocalesSettings getLocales()
    {
        return locales;
    }

    public TimeSettings getTime()
    {
        return time;
    }

    public String getNotificationsEmail()
    {
        return notificationsEmail;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @JsonIgnore
    public String getKey()
    {
        return "general";
    }

}
