/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.front.representation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;

/**
 * @version $Id$
 */
public class PriceRepresentation
{
    private String amount;

    private String amountCompact;

    private CurrencyRepresentation currency;

    public PriceRepresentation(BigDecimal amount, Currency currency, Locale locale)
    {
        MoneyFormatter formatter = new MoneyFormatterBuilder().appendAmount(MoneyAmountStyle.of(locale)).toFormatter();

        CurrencyUnit currencyUnit = CurrencyUnit.of(currency);
        this.amount = formatter.withLocale(locale)
                .print(Money.of(currencyUnit, amount, RoundingMode.HALF_EVEN));

        this.currency = new CurrencyRepresentation(currency, locale);

        if (amount.doubleValue() == amount.intValue()) {
            this.amountCompact = "" + amount.intValue();
        }
        else {
            this.amountCompact = this.amount;
        }
    }

    public String getAmount()
    {
        return amount;
    }

    public CurrencyRepresentation getCurrency()
    {
        return currency;
    }

    public String getAmountCompact()
    {
        return amountCompact;
    }
}
