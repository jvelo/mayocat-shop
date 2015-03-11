/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.taxes.internal;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.taxes.PriceWithTaxes;
import org.mayocat.shop.taxes.Taxable;
import org.mayocat.shop.taxes.TaxesService;
import org.mayocat.shop.taxes.configuration.Rate;
import org.mayocat.shop.taxes.configuration.TaxRule;
import org.mayocat.shop.taxes.configuration.TaxesSettings;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * @version $Id$
 */
@Component
public class DefaultTaxesService implements TaxesService
{
    @Inject
    private ConfigurationService configurationService;

    @Override
    public BigDecimal getVatRate(Taxable taxable)
    {
        BigDecimal itemVatRate = null;

        TaxesSettings taxesSettings = getTaxesSettings();
        BigDecimal defaultVatRate = taxesSettings.getVat().getValue().getDefaultRate();

        final Optional<String> vatRateId = taxable.getVatRateId();
        if (vatRateId.isPresent()) {
            Optional<Rate> rate = getRateForId(vatRateId.get());
            if (rate.isPresent()) {
                itemVatRate = rate.get().getValue();
            }
        } else if (taxable.getParent().isPresent() && taxable.getParent().get().isLoaded()) {
            final Taxable parent = (Taxable) taxable.getParent().get().get();
            if (parent.getVatRateId().isPresent()) {
                Optional<Rate> rate = getRateForId(parent.getVatRateId().get());
                if (rate.isPresent()) {
                    itemVatRate = rate.get().getValue();
                }
            }
        }

        if (itemVatRate == null) {
            itemVatRate = defaultVatRate;
        }

        return itemVatRate;
    }

    @Override
    public PriceWithTaxes getPriceWithTaxes(Taxable taxable)
    {
        BigDecimal itemVatRate = getVatRate(taxable);

        PriceWithTaxes itemUnit = new PriceWithTaxes(
                taxable.getUnitPrice().multiply(BigDecimal.ONE.add(itemVatRate)),
                taxable.getUnitPrice(),
                taxable.getUnitPrice().multiply(itemVatRate)
        );

        return itemUnit;
    }

    private Optional<Rate> getRateForId(final String id)
    {
        TaxesSettings taxesSettings = getTaxesSettings();
        return FluentIterable.from(taxesSettings.getVat().getValue().getOtherRates()).filter(new Predicate<Rate>()
        {
            @Override
            public boolean apply(Rate rate)
            {
                return rate.getId().equals(id);
            }
        }).first();
    }

    private TaxesSettings getTaxesSettings()
    {
        return configurationService.getSettings(TaxesSettings.class);
    }
}
