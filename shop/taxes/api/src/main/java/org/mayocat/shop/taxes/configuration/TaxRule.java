/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.taxes.configuration;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

/**
 * The rule that describe how a certain tax (for example VAT/sales tax)
 *
 * @version $Id$
 */
public class TaxRule
{
    private Optional<String> name = Optional.absent();

    @JsonProperty("geo")
    private Boolean managedGeographically = false;

    private BigDecimal defaultRate = BigDecimal.ZERO;

    private List<Rate> otherRates = Collections.emptyList();

    private List<AreaRates> areas = Collections.emptyList();

    public Optional<String> getName()
    {
        return name;
    }

    public Boolean isManagedGeographically()
    {
        return managedGeographically;
    }

    public BigDecimal getDefaultRate()
    {
        return defaultRate;
    }

    public List<Rate> getOtherRates()
    {
        return otherRates;
    }

    public List<AreaRates> getAreas()
    {
        return areas;
    }
}
