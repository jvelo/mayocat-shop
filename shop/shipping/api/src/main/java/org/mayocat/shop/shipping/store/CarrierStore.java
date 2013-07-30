package org.mayocat.shop.shipping.store;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.mayocat.shop.shipping.Strategy;
import org.mayocat.shop.shipping.model.Carrier;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface CarrierStore
{
    Carrier findById(UUID id);

    Set<Carrier> findAll();

    Set<Carrier> findAll(Strategy strategy);

    void createCarrier(Carrier carrier);

    void updateCarrier(Carrier carrier);

    void deleteCarrier(Carrier carrier);
}
