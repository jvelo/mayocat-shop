/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.mayocat.model.Association;
import org.mayocat.model.HasFeaturedImage;
import org.mayocat.model.Identifiable;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public interface Purchasable extends Identifiable, Serializable, HasFeaturedImage
{
    String getTitle();

    String getDescription();

    BigDecimal getUnitPrice();

    Optional<BigDecimal> getActualWeight();

    Optional<Association<Purchasable>> getParent();
}
