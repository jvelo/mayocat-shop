/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.web.object

import groovy.transform.CompileStatic
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.joda.money.format.MoneyFormatter
import org.joda.money.format.MoneyFormatterBuilder
import org.mayocat.shop.catalog.util.MoneyUtil

import java.math.RoundingMode

/**
 * Web object that holds the representation of a currency.
 *
 * @version $Id$
 */
@CompileStatic
class CurrencyWebObject
{
    String localSymbol;
    String symbol;

    def withCurrency(Currency currency, Locale locale)
    {
        MoneyFormatter formatter = new MoneyFormatterBuilder().appendCurrencySymbolLocalized().toFormatter();
        symbol = formatter.withLocale(locale).print(
                Money.of(CurrencyUnit.of(currency), BigDecimal.TEN, RoundingMode.HALF_EVEN));

        localSymbol = MoneyUtil.getLocalSymbol(currency);
    }
}
