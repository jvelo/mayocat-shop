/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.store.memory;

import org.mayocat.shop.billing.model.Address;
import org.mayocat.shop.billing.store.AddressStore;
import org.mayocat.store.memory.BaseEntityMemoryStore;
import org.xwiki.component.annotation.Component;

/**
 * In-memory implementation of {@link AddressStore}.
 *
 * @version $Id$
 */
@Component("memory")
public class MemoryAddressStore extends BaseEntityMemoryStore<Address> implements AddressStore
{
}
