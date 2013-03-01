package org.mayocat.shop.catalog.configuration.shop;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CatalogConfiguration
{
    @Valid
    @JsonProperty
    CurrenciesConfiguration currencies = new CurrenciesConfiguration();

    @Valid
    @JsonProperty
    ProductsConfiguration products = new ProductsConfiguration();

    @JsonIgnore
    public ProductsConfiguration getProductsConfiguration()
    {
        return products;
    }

    public CurrenciesConfiguration getCurrencies()
    {
        return currencies;
    }
}
