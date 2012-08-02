package org.mayocat.shop.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonProperty;

import com.yammer.dropwizard.config.Configuration;

public class MayocatShopConfiguration extends Configuration
{
    @Valid
    @NotNull
    @JsonProperty
    private DataSourceConfiguration dataSource = new DataSourceConfiguration();
        
    @Valid
    @NotNull
    @JsonProperty
    private MultitenancyConfiguration multitenancy = new MultitenancyConfiguration();

    public DataSourceConfiguration getDataSourceConfiguration()
    {
        return dataSource;
    }
    
    public MultitenancyConfiguration getMultitenancyConfiguration()
    {
        return multitenancy;
    }

}
