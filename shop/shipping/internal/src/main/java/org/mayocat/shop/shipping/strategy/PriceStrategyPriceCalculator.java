package org.mayocat.shop.shipping.strategy;

import java.math.BigDecimal;
import java.util.Map;

import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.StrategyPriceCalculator;
import org.mayocat.shop.shipping.model.Carrier;
import org.mayocat.shop.shipping.model.CarrierRule;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("price")
public class PriceStrategyPriceCalculator implements StrategyPriceCalculator
{
    @Override
    public BigDecimal getPrice(Carrier carrier, Map<Purchasable, Long> items)
    {
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal itemsTotal = BigDecimal.ZERO;
        for (Purchasable purchasable : items.keySet()) {
            BigDecimal itemTotal = purchasable.getUnitPrice().multiply(BigDecimal.valueOf(items.get(purchasable)));
            itemsTotal = itemsTotal.add(itemTotal);
        }

        for (CarrierRule rule : carrier.getRules()) {
            price = rule.getPrice();
            if (itemsTotal.compareTo(rule.getUpToValue()) <= 0) {
                break;
            }
        }

        return price;
    }
}
