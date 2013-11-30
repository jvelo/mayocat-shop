/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.model.Carrier;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ShippingService
{
    /**
     * @return {@code true} if shipping is enabled for the context tenant, {@code false} otherwise.
     */
    boolean isShippingEnabled();

    /**
     * Return the available shipping options for the context tenant and the passed items.
     *
     * @param items the items to get the shipping options for
     * @return the list of options for those items for the context tenant
     */
    List<ShippingOption> getOptions(Map<Purchasable, Long> items);

    /**
     * Return the shipping option for a particular carrier for some items
     *
     * @param carrierId the id of the carrier to get the option for
     * @param items the carrier to get the option for
     * @return the option for that carrier and those items
     */
    ShippingOption getOption(UUID carrierId, Map<Purchasable, Long> items);

    /**
     * Get a carrier by id. Just a short-cut to for shipping service consumers so they don't need to get injected the
     * shipping store as well.
     *
     * @param id the id of the carrier to get
     * @return the found carrier, or {@code null} if there is no carrier with that id.
     */
    Carrier getCarrier(UUID id);

    /**
     * Convert a destination code (for example "US", or "FR-49") to its "human" form (here: "United states" or "Maine et
     * Loire").
     *
     * @param destinationCode the code to get the human form for
     * @return the human readable form of the code
     */
    String getDestinationName(String destinationCode);

    /**
     * Same as {@link #getDestinationName(String)}, but for a collection of codes. The collection human values are
     * joined with a comma. For instance ["FR", "BE"] would return <code>France, Belgium</code>
     *
     * @param destinationCodes the codes to get the human form for
     * @return a string representing the destinations, destined to get presented to humans
     */
    String getDestinationNames(List<String> destinationCodes);
}
