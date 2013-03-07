package org.mayocat.configuration.theme;

import javax.validation.Valid;

import org.mayocat.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @verison $Id$
 */
public class ThemeConfiguration
{
    @Valid
    @JsonProperty
    private Configurable<String> active = new Configurable<String>("default");

    public Configurable<String> getActive()
    {
        return active;
    }
}
