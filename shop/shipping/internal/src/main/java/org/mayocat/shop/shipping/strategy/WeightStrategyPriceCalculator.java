package org.mayocat.shop.shipping.strategy;

import java.math.BigDecimal;
import java.util.Map;

import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.Purchasable;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("weight")
public class WeightStrategyPriceCalculator extends AbstractValueBasedStrategyPriceCalculator
{
    @Override
    protected BigDecimal getValue(Map<Purchasable, Long> items)
    {
        BigDecimal weight = BigDecimal.ZERO;
        for (Purchasable purchasable : items.keySet()) {
            try {
                Product product = (Product) purchasable;
                weight = weight.add(product.getWeight() != null ? product.getWeight().multiply(
                        BigDecimal.valueOf(items.get(purchasable))) : BigDecimal.ZERO);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException(
                        "Cannot calculate weight-based shipping price for something else than a product", e);
            }
        }
        return weight;
    }
}
