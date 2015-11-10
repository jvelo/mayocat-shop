/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.invoicing;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mayocat.configuration.Configurable;
import org.mayocat.configuration.ExposedSettings;

/**
 * @version $Id$
 */
public class InvoicingSettings implements ExposedSettings
{
    @JsonProperty
    private Configurable<Boolean> enabled = new Configurable<>(Boolean.FALSE);

    public Configurable<Boolean> getEnabled() {
        return enabled;
    }

    @Override
    public String getKey() {
        return "invoicing";
    }
}
