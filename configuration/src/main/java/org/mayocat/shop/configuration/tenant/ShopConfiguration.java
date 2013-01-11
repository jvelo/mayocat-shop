package org.mayocat.shop.configuration.tenant;

import javax.validation.Valid;

import org.mayocat.shop.configuration.tenant.products.CategoriesConfiguration;
import org.mayocat.shop.configuration.tenant.products.ProductsConfiguration;

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
