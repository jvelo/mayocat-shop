/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.store.memory;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.store.memory.BaseEntityMemoryStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * In-memory implementation of {@link OrderStore}
 *
 * @version $Id$
 */
@Component("memory")
public class MemoryOrderStore extends BaseEntityMemoryStore<Order> implements OrderStore
{
    private Predicate<Order> paidOrAwaitingPayment = new Predicate<Order>()
    {
        public boolean apply(@Nullable Order input)
        {
            return !Arrays.asList(Order.Status.NONE, Order.Status.PAYMENT_PENDING).contains(input.getStatus());
        }
    };

    public List<Order> findAllPaidOrAwaitingPayment(Integer number, Integer offset)
    {
        if (number == 0) {
            return FluentIterable.from(all()).filter(paidOrAwaitingPayment).skip(offset).toList();
        }
        return FluentIterable.from(all()).filter(paidOrAwaitingPayment).skip(offset).limit(number).toList();
    }

    public Order findBySlug(String order)
    {
        return FluentIterable.from(all()).filter(withSlug(order)).first().orNull();
    }
}
