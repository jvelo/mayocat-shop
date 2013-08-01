package org.mayocat.shop.shipping.strategy;

import java.math.BigDecimal;
import java.util.Map;

import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.StrategyPriceCalculator;
import org.mayocat.shop.shipping.model.Carrier;
import org.mayocat.shop.shipping.model.CarrierRule;

/**
 * @version $Id$
 */
public abstract class AbstractValueBasedStrategyPriceCalculator implements StrategyPriceCalculator
{
    @Override
    public BigDecimal getPrice(Carrier carrier, Map<Purchasable, Long> items)
    {
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal value = getValue(items);

        for (CarrierRule rule : carrier.getRules()) {
            price = rule.getPrice();
            if (value.compareTo(rule.getUpToValue()) <= 0) {
                break;
            }
        }

        return price;
    }

    protected abstract BigDecimal getValue(Map<Purchasable, Long> items);
}
