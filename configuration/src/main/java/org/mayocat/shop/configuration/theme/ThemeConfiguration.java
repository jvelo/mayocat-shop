package org.mayocat.shop.configuration.theme;

import javax.validation.Valid;

import org.mayocat.shop.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @verison $Id$
 */
public class ThemeConfiguration
{
    @Valid
    @JsonProperty
    private Configurable<String> active = new Configurable<String>("default");

    @Valid
    @JsonProperty
    private Configurable<Boolean> detectBreakpoints = new Configurable<Boolean>(true);

    public Configurable<String> getActive()
    {
        return active;
    }

    public Configurable<Boolean> getDetectBreakpoints()
    {
        return detectBreakpoints;
    }

}
