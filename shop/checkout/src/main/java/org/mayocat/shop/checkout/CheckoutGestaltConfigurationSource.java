/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout;

import javax.inject.Inject;
import org.mayocat.configuration.GestaltConfigurationSource;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;

/**
 * @version $Id$
 */
@Component("checkout")
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class CheckoutGestaltConfigurationSource implements GestaltConfigurationSource
{
    @Inject
    private CheckoutSettings checkoutSettings;

    @Override
    public Object get() {
        return checkoutSettings;
    }
}
