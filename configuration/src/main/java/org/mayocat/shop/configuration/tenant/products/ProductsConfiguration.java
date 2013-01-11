package org.mayocat.shop.configuration.tenant.products;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class ProductsConfiguration
{
    @Valid
    @JsonProperty
    private CategoriesConfiguration categories = new CategoriesConfiguration();

    public CategoriesConfiguration getCategoriesConfiguration()
    {
        return this.categories;
    }
}
