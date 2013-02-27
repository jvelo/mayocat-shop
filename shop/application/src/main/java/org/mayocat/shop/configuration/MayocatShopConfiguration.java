package org.mayocat.shop.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.mayocat.configuration.AbstractConfiguration;
import org.mayocat.configuration.general.GeneralConfiguration;
import org.mayocat.shop.catalog.configuration.shop.CatalogConfiguration;
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
    private CatalogConfiguration catalog = new CatalogConfiguration();

    public DatabaseConfiguration getDatabaseConfiguration()
    {
        return database;
    }

    public GeneralConfiguration getGeneralConfiguration()
    {
        return general;
    }

    public CatalogConfiguration getCatalogConfiguration()
    {
        return catalog;
    }

    public ThemeConfiguration getThemeConfiguration()
    {
        return theme;
    }
}
