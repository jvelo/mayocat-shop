/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping.configuration;

import org.mayocat.configuration.Configurable;
import org.mayocat.configuration.ExposedSettings;
import org.mayocat.shop.shipping.Strategy;

/**
 * @version $Id$
 */
public class ShippingSettings implements ExposedSettings
{
    private Configurable<Strategy> strategy = new Configurable<>(Strategy.NONE);

    public String getKey()
    {
        return "shipping";
    }

    public Configurable<Strategy> getStrategy()
    {
        return strategy;
    }
}
