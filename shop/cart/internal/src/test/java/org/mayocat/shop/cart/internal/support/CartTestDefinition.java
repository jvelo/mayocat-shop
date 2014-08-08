/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.internal.support;

import org.mayocat.shop.taxes.configuration.TaxesSettings;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class CartTestDefinition
{
    @JsonProperty
    private TaxesSettings taxes;

    private CartTest test;

    public TaxesSettings getTaxesSettings()
    {
        return taxes;
    }

    public CartTest getTest()
    {
        return test;
    }
}
