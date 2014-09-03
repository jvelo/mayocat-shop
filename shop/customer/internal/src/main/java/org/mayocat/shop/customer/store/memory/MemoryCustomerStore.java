/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.store.memory;

import java.util.UUID;

import javax.annotation.Nullable;

import org.mayocat.shop.customer.store.CustomerStore;
import org.mayocat.shop.customer.model.Customer;
import org.mayocat.store.memory.BaseEntityMemoryStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * In-memory implementation of {@link CustomerStore}
 *
 * @version $Id$
 */
@Component("memory")
public class MemoryCustomerStore extends BaseEntityMemoryStore<Customer> implements CustomerStore
{
    public Customer findBySlug(String slug)
    {
        return FluentIterable.from(all()).filter(withSlug(slug)).first().orNull();
    }

    @Override
    public Customer findByUserId(final UUID userId)
    {
        return FluentIterable.from(all()).filter(new Predicate<Customer>()
        {
            public boolean apply(@Nullable Customer input)
            {
                return input.getUserId().equals(userId);
            }
        }).first().orNull();
    }
}
