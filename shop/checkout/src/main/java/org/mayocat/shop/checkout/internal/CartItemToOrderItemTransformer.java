/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout.internal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.mayocat.addons.model.AddonGroupDefinition;
import org.mayocat.addons.util.AddonUtils;
import org.mayocat.configuration.PlatformSettings;
import org.mayocat.context.WebContext;
import org.mayocat.model.AddonGroup;
import org.mayocat.shop.billing.model.OrderItem;
import org.mayocat.shop.cart.CartItem;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.taxes.configuration.Rate;
import org.mayocat.shop.taxes.configuration.TaxesSettings;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class CartItemToOrderItemTransformer implements Function<CartItem, OrderItem>
{
    private PlatformSettings platformSettings;

    private ProductStore productStore;

    private TaxesSettings taxesSettings;

    private WebContext webContext;

    public CartItemToOrderItemTransformer(PlatformSettings platformSettings,
            ProductStore productStore, TaxesSettings taxesSettings, WebContext webContext)
    {
        this.platformSettings = platformSettings;
        this.productStore = productStore;
        this.taxesSettings = taxesSettings;
        this.webContext = webContext;
    }

    public OrderItem apply(final CartItem cartItem)
    {
        final Purchasable p = cartItem.item();

        String title;
        if (p.getParent().isPresent() && p.getParent().get().isLoaded()) {
            title = p.getParent().get().get().getTitle() + " - " + p.getTitle();
        } else {
            title = p.getTitle();
        }

        final String itemTitle = title;

        Product product = productStore.findById(p.getId());

        final BigDecimal vatRate;
        if (product.getVatRateId().isPresent() && getRateDefinition(product.getVatRateId().get()).isPresent()) {
            vatRate = getRateDefinition(product.getVatRateId().get()).get().getValue();
        } else {
            vatRate = taxesSettings.getVat().getValue().getDefaultRate();
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setPurchasableId(product.getId());
        orderItem.setType("product");
        orderItem.setTitle(itemTitle);
        orderItem.setQuantity(cartItem.quantity());
        orderItem.setUnitPrice(cartItem.unitPrice().incl());
        orderItem.setItemTotal(cartItem.total().incl());
        orderItem.setVatRate(vatRate);

        Map<String, Object> data = Maps.newHashMap();
        addOrderAddons(p, data);

        orderItem.addData(data);

        return orderItem;
    }

    private Optional<Rate> getRateDefinition(final String rate)
    {
        return FluentIterable.from(taxesSettings.getVat().getValue().getOtherRates()).filter(new Predicate<Rate>()
        {
            public boolean apply(Rate input)
            {
                return input.getId().equals(rate);
            }
        }).first();
    }

    private void addOrderAddons(Purchasable p, Map<String, Object> itemData)
    {
        if (Product.class.isAssignableFrom(p.getClass())) {
            Product product = (Product) p;

            if (product.getAddons().isLoaded()) {
                Map<String, Object> itemAddons = Maps.newHashMap();
                Map<String, AddonGroup> addons = product.getAddons().get();
                for (String groupName : addons.keySet()) {
                    AddonGroup addonGroup = addons.get(groupName);
                    Map<String, AddonGroupDefinition>[] sources = new Map[2];
                    sources[0] = platformSettings.getAddons();
                    if (webContext.getTheme() != null) {
                        sources[1] = webContext.getTheme().getDefinition().getAddons();
                    }
                    Optional<AddonGroupDefinition> definition = AddonUtils
                            .findAddonGroupDefinition(addonGroup.getGroup(),
                                    (Map<String, AddonGroupDefinition>[]) sources);

                    if (definition.isPresent() &&
                            definition.get().getProperties().containsKey("checkout.includeInOrder"))
                    {
                        itemAddons.put(groupName, addonGroup.getValue());
                    }
                }

                if (!itemAddons.isEmpty()) {
                    itemData.put("addons", itemAddons);
                }
            }
        }
    }
}
