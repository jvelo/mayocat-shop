package org.mayocat.shop.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.mayocat.configuration.AbstractConfiguration;
import org.mayocat.configuration.general.GeneralConfiguration;
import org.mayocat.configuration.shop.ShopConfiguration;
import org.mayocat.configuration.theme.ThemeConfiguration;

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
    private ThemeConfiguration theme = new ThemeConfiguration();

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

    public ShopConfiguration getShop()
    {
        return shop;
    }
}
