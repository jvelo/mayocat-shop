/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.internal.support;

import org.mayocat.shop.taxes.PriceWithTaxes;

/**
 * @version $Id$
 */
public class ItemExpectation
{
    private PriceWithTaxes unitPrice;

    private PriceWithTaxes total;

    public PriceWithTaxes getUnitPrice()
    {
        return unitPrice;
    }

    public PriceWithTaxes getTotal()
    {
        return total;
    }
}
