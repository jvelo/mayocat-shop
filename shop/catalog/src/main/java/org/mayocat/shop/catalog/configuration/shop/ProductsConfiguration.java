package org.mayocat.shop.catalog.configuration.shop;

import javax.validation.Valid;

import org.mayocat.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class ProductsConfiguration
{
    @Valid
    @JsonProperty
    private Configurable<Boolean> stock = new Configurable<Boolean>(true);

    @Valid
    @JsonProperty
    private Configurable<Boolean> categories = new Configurable<Boolean>(true);

    @Valid
    @JsonProperty
    private Configurable<Boolean> variants = new Configurable<Boolean>(true);

    @Valid
    @JsonProperty
    private Configurable<Boolean> priceVariesWithVariants = new Configurable<Boolean>(true);


    public Configurable<Boolean> getCategories()
    {
        return this.categories;
    }

    public Configurable<Boolean> getVariants()
    {
        return variants;
    }

    public Configurable<Boolean> getPriceVariesWithVariants()
    {
        return priceVariesWithVariants;
    }

    public Configurable<Boolean> getStock()
    {
        return stock;
    }
}
