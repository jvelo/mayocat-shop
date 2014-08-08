/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import org.mayocat.shop.shipping.ShippingOption;
import org.mayocat.shop.taxes.Taxable;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 *
 * A serialization size optimized version of {@link CartContents}. We need this since cookie size is limited in
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

    private String currency;

    private ShippingOption selectedOption;

    public CartInSession(CartContents cartContents)
    {
        for (Taxable item : cartContents.getItems().keySet()) {
            items.put(new IdAndType(item.getId(), item.getClass().getCanonicalName()), cartContents.getItems().get(item));
        }
        this.currency = cartContents.getCurrency().getCurrencyCode();
        this.selectedOption = cartContents.getSelectedShippingOption();
    }

    public Map<IdAndType, Long> getItems()
    {
        return items;
    }

    public String getCurrency()
    {
        return currency;
    }

    public ShippingOption getSelectedOption()
    {
        return selectedOption;
    }
}
