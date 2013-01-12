package org.mayocat.shop.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.mayocat.shop.configuration.tenant.ShopConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;

public class MayocatShopConfiguration extends AbstractConfiguration
{
    @Valid
    @NotNull
    @JsonProperty
    private DatabaseConfiguration database = new DatabaseConfiguration();

    public DatabaseConfiguration getDatabaseConfiguration()
    {
        return database;
    }

}
