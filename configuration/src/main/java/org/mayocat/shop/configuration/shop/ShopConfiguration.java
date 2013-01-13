package org.mayocat.shop.configuration.shop;

import javax.validation.Valid;

import org.mayocat.shop.configuration.Configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShopConfiguration implements Configuration
{
    @Valid
    @JsonProperty
    ProductsConfiguration products = new ProductsConfiguration();

    @JsonIgnore
    public ProductsConfiguration getProductsConfiguration()
    {
        return products;
    }
}
