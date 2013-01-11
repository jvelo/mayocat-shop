package org.mayocat.shop.configuration.tenant.products;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class CategoriesConfiguration
{
    @Valid
    @JsonProperty
    boolean enabled;

    @Valid
    @JsonProperty
    boolean configurable;

    public boolean isEnabled()
    {
        return enabled;
    }

    public boolean isConfigurable()
    {
        return configurable;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
