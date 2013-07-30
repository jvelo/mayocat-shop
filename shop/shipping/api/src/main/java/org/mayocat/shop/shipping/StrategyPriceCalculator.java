package org.mayocat.shop.shipping;

import java.math.BigDecimal;
import java.util.Map;

import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.model.Carrier;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface StrategyPriceCalculator
{
    BigDecimal getPrice(Carrier carrier, Map<Purchasable, Long> items);
}
