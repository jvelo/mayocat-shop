/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.taxes.configuration;

import java.math.BigDecimal;

import com.google.common.base.Optional;

/**
 * A tax rate, optionally named
 *
 * @version $Id$
 */
public class Rate
{
    /**
     * The value for this rate as a decimal factor (for example, "0.1" for 10%).
     */
    private BigDecimal value = BigDecimal.ZERO;

    /**
     * An optional name for the rate. It can describe for example the kind of products it is applied to.
     */
    private Optional<String> name;

    private String id;

    public BigDecimal getValue()
    {
        return value;
    }

    public Optional<String> getName()
    {
        return name;
    }

    public String getId()
    {
        return id;
    }
}
