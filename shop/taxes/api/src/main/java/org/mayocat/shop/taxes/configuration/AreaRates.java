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

/**
 * Tax rates for a certain tax for a certain geographical area (or combination of areas), when said tax is managed
 * geographically.
 *
 * @version $Id$
 */
public class AreaRates
{
    /**
     * The name given to that area ; for example "European Union" or "US except Alaska", etc.
     */
    private String name;

    /**
     * The default rate applied for this area.
     */
    private BigDecimal defaultRate = BigDecimal.ZERO;

    /**
     * Possible other rates applied in this area (example: in France, rates for printed books, or restaurants, etc.)
     */
    private List<Rate> otherRates = Collections.emptyList();

    /**
     * The ISO 3166 codes of geographical areas that compose this ares. For example : "FR", "BE"
     */
    private List<String> codes;

    public String getName()
    {
        return name;
    }

    public BigDecimal getDefaultRate()
    {
        return defaultRate;
    }

    public List<Rate> getOtherRates()
    {
        return otherRates;
    }

    public List<String> getCodes()
    {
        return codes;
    }
}
