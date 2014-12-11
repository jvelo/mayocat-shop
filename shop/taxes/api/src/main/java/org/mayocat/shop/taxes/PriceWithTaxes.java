/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.taxes;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * Represents a price with taxes. Right now only VAT is supported.
 *
 * This is a container, immutable object.
 *
 * PriceWithTaxes objects holds strictly no information about how the taxes were calculated (what rate, what country,
 * etc.), they just hold the inclusive, exclusive and tax amounts.
 *
 * @version $Id$
 */
public class PriceWithTaxes implements Serializable
{
    private final BigDecimal incl;

    private final BigDecimal excl;

    private final BigDecimal vat;

    @JsonCreator
    public PriceWithTaxes(
            @JsonProperty("incl") BigDecimal incl,
            @JsonProperty("excl") BigDecimal excl,
            @JsonProperty("vat") BigDecimal vat
    )
    {
        this.incl = incl;
        this.excl = excl;
        this.vat = vat;
    }

    public BigDecimal excl()
    {
        return excl;
    }

    public BigDecimal incl()
    {
        return incl;
    }

    public BigDecimal vat()
    {
        return vat;
    }

    //

    public PriceWithTaxes add(PriceWithTaxes price)
    {
        return new PriceWithTaxes(
                this.incl().add(price.incl()),
                this.excl().add(price.excl()),
                this.vat().add(price.vat())
        );
    }

    public PriceWithTaxes multiply(Long quantity)
    {
        return new PriceWithTaxes(
                this.incl().multiply(BigDecimal.valueOf(quantity)),
                this.excl().multiply(BigDecimal.valueOf(quantity)),
                this.vat().multiply(BigDecimal.valueOf(quantity))
        );
    }

    //

    @Override
    public int hashCode()
    {
        return Objects.hashCode(
                this.incl,
                this.excl,
                this.vat
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
        final PriceWithTaxes other = (PriceWithTaxes) obj;

        return Objects.equal(this.incl, other.incl)
                && Objects.equal(this.excl, other.excl)
                && Objects.equal(this.vat, other.vat);
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}
