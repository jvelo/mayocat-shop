package org.mayocat.shop.catalog.web.object

import groovy.transform.CompileStatic
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.joda.money.format.MoneyAmountStyle
import org.joda.money.format.MoneyFormatter
import org.joda.money.format.MoneyFormatterBuilder

import java.math.RoundingMode

/**
 * Web object for a price representation. Contains the amount formatted according to a passed locale and a currency
 * representation.
 *
 * @version $Id$
 */
@CompileStatic
class PriceWebObject
{
    String amount;
    String amountCompact;
    CurrencyWebObject currency;

    def withPrice(BigDecimal price, Currency priceCurrency, Locale locale)
    {
        MoneyFormatter formatter = new MoneyFormatterBuilder().appendAmount(MoneyAmountStyle.of(locale)).toFormatter();

        CurrencyUnit currencyUnit = CurrencyUnit.of(priceCurrency);
        amount = formatter.withLocale(locale).print(Money.of(currencyUnit, price, RoundingMode.HALF_EVEN));

        currencyUnit.getDecimalPlaces();

        currency = new CurrencyWebObject();
        currency.withCurrency(priceCurrency, locale)

        if (price.doubleValue() == price.intValue()) {
            amountCompact = "" + price.intValue();
        } else {
            amountCompact = amount;
        }
    }
}
