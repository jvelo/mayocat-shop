/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.store.memory;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.InvalidEntityException;

/**
 * Tests for {@link MemoryOrderStore}
 *
 * @version $Id$
 */
public class MemoryOrderStoreTest
{
    private OrderStore orderStore;

    @Before
    public void setUpStore()
    {
        orderStore = new MemoryOrderStore();
    }

    @Test
    public void testFindAllPaidOrAwaitingPayment() throws InvalidEntityException, EntityAlreadyExistsException
    {
        Order order1 = new Order();
        order1.setStatus(Order.Status.NONE);
        order1 = orderStore.create(order1);

        Order order2 = new Order();
        order2.setStatus(Order.Status.CANCELLED);
        order2 = orderStore.create(order2);

        Order order3 = new Order();
        order3.setStatus(Order.Status.PAID);
        order3 = orderStore.create(order3);

        Order order4 = new Order();
        order4.setStatus(Order.Status.PAYMENT_PENDING);
        order4 = orderStore.create(order4);

        Assert.assertEquals(2, orderStore.findAllPaidOrAwaitingPayment(0, 0).size());
        Assert.assertTrue(Arrays.asList(order2, order3).contains(orderStore.findAllPaidOrAwaitingPayment(0, 0).get(0)));
        Assert.assertTrue(Arrays.asList(order2, order3).contains(orderStore.findAllPaidOrAwaitingPayment(0, 0).get(1)));
    }
}
