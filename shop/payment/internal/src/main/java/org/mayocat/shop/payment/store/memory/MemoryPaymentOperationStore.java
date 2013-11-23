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
