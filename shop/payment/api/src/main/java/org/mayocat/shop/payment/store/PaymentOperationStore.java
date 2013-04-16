package org.mayocat.shop.payment.store;

import org.mayocat.shop.payment.model.PaymentOperation;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface PaymentOperationStore extends Store<PaymentOperation, Long>
{
}
