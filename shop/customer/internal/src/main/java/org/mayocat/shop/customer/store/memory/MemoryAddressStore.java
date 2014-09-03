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

import org.mayocat.shop.customer.model.Customer;
import org.mayocat.shop.customer.store.AddressStore;
import org.mayocat.shop.customer.model.Address;
import org.mayocat.store.memory.BaseEntityMemoryStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * In-memory implementation of {@link AddressStore}.
 *
 * @version $Id$
 */
@Component("memory")
public class MemoryAddressStore extends BaseEntityMemoryStore<Address> implements AddressStore
{
    @Override
    public Address findByCustomerIdAndType(final UUID customerId, final String type)
    {

        return FluentIterable.from(all()).filter(new Predicate<Address>()
        {
            public boolean apply(@Nullable Address input)
            {
                return input.getCustomerId().equals(customerId) && input.getType().equals(type);
            }
        }).first().orNull();
    }
}
