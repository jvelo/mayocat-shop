/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.taxes;

import org.mayocat.shop.catalog.model.Purchasable;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public interface Taxable extends Purchasable
{
    /**
     * Gets an optional ID of the rate to be applied for this taxable (product, service, etc.).
     * If no ID is returned (absent option), it means the default VAT rate is to be applied.
     */
    Optional<String> getVATRateId();
}
