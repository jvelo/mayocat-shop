package org.mayocat.shop.configuration;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class MultitenancyConfiguration extends Configuration
{
    @Valid
    @JsonProperty
    private boolean activated = false;

    @Valid
    @JsonProperty
    private String rootDomain = null;
    
    @Valid
    @JsonProperty
    private String defaultTenant = "default";

    @Valid
    @JsonProperty
    private String resolver = "default";
    
    public boolean isActivated()
    {
        return activated;
    }

    public String getRootDomain()
    {
        return rootDomain;
    }

    public String getDefaultTenant()
    {
        return defaultTenant;
    }

    public String getResolver()
    {
        return resolver;
    }
}
