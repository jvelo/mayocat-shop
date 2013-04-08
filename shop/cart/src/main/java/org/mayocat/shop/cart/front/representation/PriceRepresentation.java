package org.mayocat.shop.cart.front.representation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

        currencyUnit.getDecimalPlaces();

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
