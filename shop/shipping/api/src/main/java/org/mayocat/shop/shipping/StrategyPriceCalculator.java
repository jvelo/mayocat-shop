/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping;

import java.math.BigDecimal;
import java.util.Map;

import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.model.Carrier;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface StrategyPriceCalculator
{
    BigDecimal getPrice(Carrier carrier, Map<Purchasable, Long> items);
}
