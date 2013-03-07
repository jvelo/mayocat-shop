package org.mayocat.configuration.theme;

import javax.validation.Valid;

import org.mayocat.base.ExposedSettings;
import org.mayocat.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @verison $Id$
 */
public class ThemeSettings implements ExposedSettings
{
    @Valid
    @JsonProperty
    private Configurable<String> active = new Configurable<String>("default");

    public Configurable<String> getActive()
    {
        return active;
    }

    @JsonIgnore
    @Override
    public String getKey()
    {
        return "theme";
    }
}
