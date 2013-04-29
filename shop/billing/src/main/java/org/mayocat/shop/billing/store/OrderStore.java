package org.mayocat.shop.billing.store;

import java.util.List;
import java.util.UUID;

import org.mayocat.shop.billing.model.Order;
import org.mayocat.store.EntityStore;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface OrderStore extends Store<Order, UUID>, EntityStore
{
    List<Order> findAllWithStatus(Integer number, Integer offset);

    Order findBySlug(String order);

}
