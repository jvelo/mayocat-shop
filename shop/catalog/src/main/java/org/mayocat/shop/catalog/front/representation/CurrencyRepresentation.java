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
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;
import org.mayocat.shop.catalog.util.MoneyUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @version $Id$
 */
public class CurrencyRepresentation
{
    @JsonIgnore
    private static final MoneyFormatter CURRENCY_FORMATTER = new MoneyFormatterBuilder().
            appendCurrencySymbolLocalized().
            toFormatter();

    private String localSymbol;

    private String symbol;

    public CurrencyRepresentation(Currency currency, Locale locale)
    {
        this.symbol = CURRENCY_FORMATTER.withLocale(locale).print(
                Money.of(CurrencyUnit.of(currency), BigDecimal.TEN, RoundingMode.HALF_EVEN));

        this.localSymbol = MoneyUtil.getLocalSymbol(currency);
    }

    public String getLocalSymbol()
    {
        return localSymbol;
    }

    public String getSymbol()
    {
        return symbol;
    }
}
