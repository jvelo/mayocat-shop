package org.mayocat.shop.catalog.configuration.shop;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.joda.money.CurrencyUnit;
import org.mayocat.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class CurrenciesConfiguration
{

    @Valid
    @JsonProperty("main")
    private Configurable<CurrencyUnit> mainCurrency = new Configurable(CurrencyUnit.EUR);

    @Valid
    @JsonProperty("others")
    private Configurable<List<CurrencyUnit>> otherCurrencies = new Configurable(Collections.emptyList());

    public Configurable<CurrencyUnit> getMainCurrency()
    {
        return mainCurrency;
    }

    public Configurable<List<CurrencyUnit>> getOtherCurrencies()
    {
        return otherCurrencies;
    }
}
