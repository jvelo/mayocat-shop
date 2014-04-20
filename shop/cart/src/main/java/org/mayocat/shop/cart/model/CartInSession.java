/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.model;

import java.io.Serializable;
import java.util.Currency;
import java.util.Map;
import java.util.UUID;

import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.ShippingOption;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 *
 * A serialization size optimized version of {@link Cart}. We need this since cookie size is limited in
 * browsers, so we just can't serialize the whole purchasables (products, etc.).
 */
public class CartInSession implements Serializable
{
    public static class IdAndType implements Serializable
    {
        private UUID id;

        private String type;

        private IdAndType(UUID id, String type)
        {
            this.id = id;
            this.type = type;
        }

        public UUID getId()
        {
            return id;
        }

        public String getType()
        {
            return type;
        }
    }

    private Map<IdAndType, Long> items = Maps.newLinkedHashMap();

    private Currency currency;

    private ShippingOption selectedOption;

    public CartInSession(Cart cart)
    {
        for (Purchasable item : cart.getItems().keySet()) {
            items.put(new IdAndType(item.getId(), item.getClass().getCanonicalName()), cart.getItems().get(item));
        }
        this.currency = cart.getCurrency();
        this.selectedOption = cart.getSelectedShippingOption();
    }

    public Map<IdAndType, Long> getItems()
    {
        return items;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public ShippingOption getSelectedOption()
    {
        return selectedOption;
    }
}
