package org.mayocat.shop.catalog.web.object

import groovy.transform.CompileStatic
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.joda.money.format.MoneyFormatter
import org.joda.money.format.MoneyFormatterBuilder
import org.mayocat.shop.catalog.util.MoneyUtil

import java.math.RoundingMode

/**
 * Doc goes here.
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
