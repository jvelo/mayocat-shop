/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.store.memory;

import org.mayocat.shop.payment.model.PaymentOperation;
import org.mayocat.shop.payment.store.PaymentOperationStore;
import org.mayocat.store.memory.BaseEntityMemoryStore;
import org.xwiki.component.annotation.Component;

/**
 * In-memory implementation of {@link PaymentOperationStore}
 *
 * @version $Id$
 */
@Component("memory")
public class MemoryPaymentOperationStore extends BaseEntityMemoryStore<PaymentOperation> implements
        PaymentOperationStore
{
}
