package org.mayocat.shop.cart.front.representation;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * @version $Id$
 */
public class PriceRepresentation
{
    private String amount;

    private CurrencyRepresentation currency;

    public PriceRepresentation(String amount, CurrencyRepresentation currency)
    {
        this.amount = amount;
        this.currency = currency;
    }

    public String getAmount()
    {
        return amount;
    }

    public CurrencyRepresentation getCurrency()
    {
        return currency;
    }
}
