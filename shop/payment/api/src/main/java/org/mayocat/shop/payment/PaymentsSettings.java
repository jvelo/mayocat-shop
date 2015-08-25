/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment;

import com.google.common.collect.Lists;
import java.util.List;
import org.mayocat.configuration.Configurable;
import org.mayocat.configuration.ExposedSettings;

/**
 * @version $Id$
 */
public class PaymentsSettings implements ExposedSettings
{
    @Override
    public String getKey() {
        return "payments";
    }

    private Configurable<List<String>> availableGateways =
            new Configurable<List<String>>(Lists.newArrayList("paypaladaptivepayments"));

    public Configurable<List<String>> getAvailableGateways() {
        return availableGateways;
    }
}
