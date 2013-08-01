package org.mayocat.shop.shipping.strategy;

import java.math.BigDecimal;
import java.util.Map;

import org.mayocat.shop.catalog.model.Purchasable;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("price")
public class PriceStrategyPriceCalculator extends AbstractValueBasedStrategyPriceCalculator
{
    @Override
    protected BigDecimal getValue(Map<Purchasable, Long> items)
    {
        BigDecimal itemsTotal = BigDecimal.ZERO;
        for (Purchasable purchasable : items.keySet()) {
            BigDecimal itemTotal = purchasable.getUnitPrice().multiply(BigDecimal.valueOf(items.get(purchasable)));
            itemsTotal = itemsTotal.add(itemTotal);
        }
        return itemsTotal;
    }
}
