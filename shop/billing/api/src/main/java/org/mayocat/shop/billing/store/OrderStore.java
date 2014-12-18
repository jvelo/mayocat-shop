/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.store;

import java.util.Date;
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

    /**
     * Lists all paid orders for a customer.
     *
     * @return the matched orders
     */
    List<Order> findAllPaidForCustomer(UUID customerId);

    /**
     * Lists all paid orders for a customer.
     *
     * @param number the number of orders to bring back
     * @param offset the offset at which to start finding the orders at
     * @return the matched orders
     */
    List<Order> findAllPaidForCustomer(UUID customerId, Integer number, Integer offset);

    /**
     * @return the total count for {@link #findAllPaidForCustomer(UUID, Integer, Integer)}.
     */
    Integer countAllPaidForCustomer(UUID customerId);

    /**
     * @return the total count for {@link #findAllPaidOrAwaitingPayment(Integer, Integer)}.
     */
    Integer countAllPaidOrAwaitingPayment();

    /**
     * Finds all orders paids within a period of time
     * @param date1 the left bound of the period of time to match
     * @param date2 the right bound of the period of time to match
     * @return the found orders for that period of time
     */
    List<Order> findAllPaidBetween(Date date1, Date date2);

    Order findBySlug(String order);
}
