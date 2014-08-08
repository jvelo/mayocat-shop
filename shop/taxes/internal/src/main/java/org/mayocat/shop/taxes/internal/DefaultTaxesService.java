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
import org.mayocat.shop.taxes.configuration.TaxesSettings;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultTaxesService implements TaxesService
{
    @Inject
    private ConfigurationService configurationService;

    @Override
    public PriceWithTaxes getPriceWithTaxes(Taxable taxable)
    {
        TaxesSettings taxesSettings = getTaxesSettings();
        BigDecimal defaultVatRate = taxesSettings.getVat().getValue().getDefaultRate();

        PriceWithTaxes itemUnit = new PriceWithTaxes(
                taxable.getUnitPrice().multiply(BigDecimal.ONE.add(defaultVatRate)),
                taxable.getUnitPrice(),
                taxable.getUnitPrice().multiply(defaultVatRate)
        );

        return itemUnit;

    }

    private TaxesSettings getTaxesSettings()
    {
        return configurationService.getSettings(TaxesSettings.class);
    }
}
