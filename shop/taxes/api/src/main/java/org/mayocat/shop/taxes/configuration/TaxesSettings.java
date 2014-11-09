/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.taxes.configuration;

import java.util.Collections;
import java.util.List;

import org.mayocat.configuration.Configurable;
import org.mayocat.configuration.ExposedSettings;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class TaxesSettings implements ExposedSettings
{
    @JsonProperty
    private Configurable<Mode> mode = new Configurable<>(Mode.INCLUSIVE_OF_TAXES);

    @JsonProperty
    private Configurable<TaxRule> vat = new Configurable<>(new TaxRule());

    @JsonProperty
    private Configurable<List<TaxRule>> others = new Configurable(Collections.emptyList());

    @Override
    public String getKey()
    {
        return "taxes";
    }

    public Configurable<Mode> getMode()
    {
        return mode;
    }

    public Configurable<TaxRule> getVat()
    {
        return vat;
    }

    public Configurable<List<TaxRule>> getOthers()
    {
        return others;
    }
}
