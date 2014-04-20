/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.util;

import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Utility class related to money and currencies
 *
 * @version $Id$
 */
public class MoneyUtil
{
    public static SortedMap<Currency, Locale> currencyLocaleMap;

    static {
        currencyLocaleMap = new TreeMap<Currency, Locale>(new Comparator<Currency>()
        {
            public int compare(Currency c1, Currency c2)
            {
                return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
            }
        });
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                currencyLocaleMap.put(currency, locale);
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }
    }

    /**
     * Always returns the local symbol (for example $ or the euro symbol) of a currency, whatever the locale of the
     * system is.
     *
     * @param currency the currency to get the local symbol
     * @return the local symbol for the passed currency
     */
    public static String getLocalSymbol(Currency currency)
    {
        return currencyLocaleMap.containsKey(currency) ? currency.getSymbol(currencyLocaleMap.get(currency)) :
                currency.getSymbol();
    }
}
