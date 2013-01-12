package org.mayocat.shop.configuration.shop;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShopConfiguration
{
    @Valid
    @JsonProperty
    ProductsConfiguration products;

    public ProductsConfiguration getProductsConfiguration()
    {
        return products;
    }
}
