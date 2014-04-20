/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import com.google.common.base.Objects;

/**
 * @version $Id$
 */
public class ShippingOption implements Serializable
{
    private UUID carrierId;

    private String title;

    private BigDecimal price;

    public ShippingOption(UUID carrierId, String title, BigDecimal price)
    {
        this.carrierId = carrierId;
        this.title = title;
        this.price = price;
    }

    public String getTitle()
    {
        return title;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public UUID getCarrierId()
    {
        return carrierId;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(
                this.carrierId,
                this.title,
                this.price
        );
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ShippingOption other = (ShippingOption) obj;

        return Objects.equal(this.carrierId, other.carrierId);
    }
}
