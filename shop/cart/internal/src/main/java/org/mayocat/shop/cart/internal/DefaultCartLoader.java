/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.internal;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.store.TenantStore;
import org.mayocat.context.WebContext;
import org.mayocat.shop.cart.Cart;
import org.mayocat.shop.cart.CartBuilder;
import org.mayocat.shop.cart.CartContents;
import org.mayocat.shop.cart.CartItemBuilder;
import org.mayocat.shop.cart.CartLoader;
import org.mayocat.shop.taxes.PriceWithTaxes;
import org.mayocat.shop.taxes.Taxable;
import org.mayocat.shop.taxes.TaxesService;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultCartLoader implements CartLoader
{
    @Inject
    private TaxesService taxesService;

    @Inject
    private WebContext webContext;

    @Inject
    private Provider<TenantStore> tenantStore;

    @Inject
    private Logger logger;

    @Override
    public Cart load(CartContents contents)
    {
        CartBuilder builder = new CartBuilder();

        builder.currency(contents.getCurrency());

        PriceWithTaxes itemsTotal = new PriceWithTaxes(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        for (Taxable taxable : contents.getItems().keySet()) {
            CartItemBuilder itemBuilder = new CartItemBuilder();
            Long quantity = contents.getItems().get(taxable);

            PriceWithTaxes itemUnit = taxesService.getPriceWithTaxes(taxable);

            itemsTotal = itemsTotal.add(itemUnit.multiply(quantity));

            itemBuilder
                    .item(taxable)
                    .quantity(quantity)
                    .unitPrice(itemUnit);

            if (webContext.getTenant() != null) {
                itemBuilder.tenant(webContext.getTenant());
            } else {
                Tenant tenant = tenantStore.get().findById(taxable.getTenantId());
                if (tenant == null) {
                    logger.error("Cannot load purchasable for which the tenant can't be found");
                    continue;
                }
                itemBuilder.tenant(tenant);
            }

            builder.addItem(itemBuilder.build());
        }

        builder.itemsTotal(itemsTotal);

        return builder.build();
    }
}
