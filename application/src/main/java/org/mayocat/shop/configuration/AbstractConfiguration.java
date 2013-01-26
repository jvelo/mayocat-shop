package org.mayocat.shop.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.mayocat.shop.configuration.shop.ShopConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

/**
 * @version $Id$
 */
public class AbstractConfiguration extends Configuration
{
    @Valid
    @NotNull
    @JsonProperty
    private DataSourceConfiguration dataSource = new DataSourceConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private MultitenancyConfiguration multitenancy = new MultitenancyConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private AuthenticationConfiguration authentication = new AuthenticationConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private SearchEngineConfiguration searchEngine = new SearchEngineConfiguration();

    public DataSourceConfiguration getDataSourceConfiguration()
    {
        return dataSource;
    }

    public MultitenancyConfiguration getMultitenancyConfiguration()
    {
        return multitenancy;
    }

    public AuthenticationConfiguration getAuthenticationConfiguration()
    {
        return authentication;
    }

    public SearchEngineConfiguration getSearchEngine()
    {
        return searchEngine;
    }

}
