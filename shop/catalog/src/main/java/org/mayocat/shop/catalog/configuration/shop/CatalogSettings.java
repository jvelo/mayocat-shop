package org.mayocat.shop.catalog.configuration.shop;

import javax.validation.Valid;

import org.mayocat.base.ExposedSettings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CatalogSettings implements ExposedSettings
{
    @Valid
    @JsonProperty
    CurrenciesSettings currencies = new CurrenciesSettings();

    @Valid
    @JsonProperty
    ProductsSettings products = new ProductsSettings();

    @JsonIgnore
    public ProductsSettings getProductsSettings()
    {
        return products;
    }

    public CurrenciesSettings getCurrencies()
    {
        return currencies;
    }

    @Override
    @JsonIgnore
    public String getKey()
    {
        return "catalog";
    }
}
