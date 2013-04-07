package org.mayocat.shop.cart.front.representation;

import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

import org.joda.money.Money;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;

/**
 * @version $Id$
 */
public class CurrencyRepresentation
{
    private String localSymbol;

    private String symbol;

    public CurrencyRepresentation(String symbol, String localSymbol)
    {
        this.symbol = symbol;
        this.localSymbol = localSymbol;
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
