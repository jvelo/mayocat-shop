/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.configuration.shop;

import javax.validation.Valid;

import org.mayocat.configuration.ExposedSettings;

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
