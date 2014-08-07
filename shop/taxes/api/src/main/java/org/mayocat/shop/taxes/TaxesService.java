package org.mayocat.shop.taxes;

import java.math.BigDecimal;

import org.mayocat.shop.taxes.configuration.TaxesSettings;

/**
 * @version $Id$
 */
public interface TaxesService
{
    TaxesSettings getSettings();

    BigDecimal getVATRate(HasTaxes taxable);

    BigDecimal getVATRate(HasTaxes taxable, String iso3166Code);

    BigDecimal getTaxRate(HasTaxes taxable, String taxSlug);

    BigDecimal getTaxRate(HasTaxes taxable, String taxSlug, String iso3166Code);
}
