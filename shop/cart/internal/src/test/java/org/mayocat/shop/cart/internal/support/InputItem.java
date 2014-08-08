/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.internal.support;

import java.math.BigDecimal;

/**
 * @version $Id$
 */
public class InputItem
{
    private BigDecimal unitPrice;

    private Long quantity;

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public Long getQuantity()
    {
        return quantity;
    }
}
