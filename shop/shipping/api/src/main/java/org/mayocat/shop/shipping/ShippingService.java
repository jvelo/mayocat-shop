package org.mayocat.shop.shipping;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.mayocat.shop.catalog.model.Purchasable;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ShippingService
{
    boolean isShippingEnabled();

    List<ShippingOption> getOptions(Map<Purchasable, Long> items);

    ShippingOption getOption(UUID carrierId, Map<Purchasable, Long> items);
}
