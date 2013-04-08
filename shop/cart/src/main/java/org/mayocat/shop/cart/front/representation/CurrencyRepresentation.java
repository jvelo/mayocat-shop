package org.mayocat.shop.cart.front.representation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;

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

        this.localSymbol = symbol;
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
