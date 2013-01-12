package org.mayocat.shop.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.mayocat.shop.configuration.general.GeneralConfiguration;
import org.mayocat.shop.configuration.shop.ShopConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.db.DatabaseConfiguration;

public class MayocatShopConfiguration extends AbstractConfiguration
{
    @Valid
    @NotNull
    @JsonProperty
    private DatabaseConfiguration database = new DatabaseConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private GeneralConfiguration general = new GeneralConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private ShopConfiguration shop = new ShopConfiguration();

    public DatabaseConfiguration getDatabaseConfiguration()
    {
        return database;
    }

    public GeneralConfiguration getGeneralConfiguration()
    {
        return general;
    }

    public ShopConfiguration getShopConfiguration()
    {
        return shop;
    }
}
