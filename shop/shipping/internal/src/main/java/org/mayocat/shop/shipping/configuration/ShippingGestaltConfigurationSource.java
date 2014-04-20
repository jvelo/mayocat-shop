/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping.configuration;

import javax.inject.Inject;

import org.mayocat.configuration.GestaltConfigurationSource;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("shipping")
public class ShippingGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private ShippingSettings shippingSettings;

    @Override
    public Object get()
    {
        return shippingSettings;
    }
}
