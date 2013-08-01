package org.mayocat.shop.catalog.configuration.shop;

import javax.validation.Valid;

import org.mayocat.configuration.Configurable;
import org.mayocat.shop.catalog.model.WeightUnit;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class ProductsSettings
{
    @Valid
    @JsonProperty
    private Configurable<Boolean> stock = new Configurable<Boolean>(true);

    @Valid
    @JsonProperty
    private Configurable<Boolean> collections = new Configurable<Boolean>(true);

    @Valid
    @JsonProperty
    private Configurable<Boolean> weight = new Configurable<Boolean>(true);

    @Valid
    @JsonProperty
    private Configurable<WeightUnit> weightUnit = new Configurable<WeightUnit>(WeightUnit.KILOGRAM);

    public Configurable<Boolean> getCollections()
    {
        return this.collections;
    }

    public Configurable<Boolean> getStock()
    {
        return stock;
    }

    public Configurable<Boolean> getWeight()
    {
        return weight;
    }

    public Configurable<WeightUnit> getWeightUnit()
    {
        return weightUnit;
    }
}
