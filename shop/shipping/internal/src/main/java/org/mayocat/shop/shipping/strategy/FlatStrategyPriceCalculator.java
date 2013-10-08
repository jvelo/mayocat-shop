package org.mayocat.shop.shipping.strategy;

import java.math.BigDecimal;
import java.util.Map;

import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.StrategyPriceCalculator;
import org.mayocat.shop.shipping.model.Carrier;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("flat")
public class FlatStrategyPriceCalculator implements StrategyPriceCalculator
{
    @Override
    public BigDecimal getPrice(Carrier carrier, Map<Purchasable, Long> items)
    {
        BigDecimal price = carrier.getPerShipping();
        Long numberOfItems = 0l;
        for (Long number : items.values()) {
            numberOfItems += number;
        }

        if (numberOfItems == 0) {
            // If the cart is empty, there's no shipping to be paid
            return BigDecimal.ZERO;
        }

        price = price.add(carrier.getPerItem().multiply(BigDecimal.valueOf(numberOfItems)));
        return price;
    }
}
