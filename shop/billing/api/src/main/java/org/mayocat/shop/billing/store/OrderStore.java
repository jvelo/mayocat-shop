/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
    /**
     * Lists all order that have a status different than {@link Order.Status#NONE} and {@link
     * Order.Status#PAYMENT_PENDING}
     *
     * @param number the number of orders to bring back
     * @param offset the offset at which to start finding the orders at
     * @return the matched orders
     */
    List<Order> findAllPaidOrAwaitingPayment(Integer number, Integer offset);

    Order findBySlug(String order);
}
