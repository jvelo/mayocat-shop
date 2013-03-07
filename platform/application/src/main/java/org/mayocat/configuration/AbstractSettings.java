package org.mayocat.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

/**
 * @version $Id$
 */
public class AbstractSettings extends Configuration
{
    @Valid
    @NotNull
    @JsonProperty
    private PlatformSettings platform = new PlatformSettings();

    @Valid
    @NotNull
    @JsonProperty
    private MultitenancySettings multitenancy = new MultitenancySettings();

    @Valid
    @NotNull
    @JsonProperty
    private AuthenticationSettings authentication = new AuthenticationSettings();

    @Valid
    @NotNull
    @JsonProperty
    private SearchEngineSettings searchEngine = new SearchEngineSettings();

    public MultitenancySettings getMultitenancySettings()
    {
        return multitenancy;
    }

    public AuthenticationSettings getAuthenticationSettings()
    {
        return authentication;
    }

    public SearchEngineSettings getSearchEngineSettings()
    {
        return searchEngine;
    }

    public PlatformSettings getPlatformSettings()
    {
        return platform;
    }
}
