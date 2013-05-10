package org.mayocat.shop.payment.store;

import java.util.UUID;

import org.mayocat.shop.payment.model.PaymentOperation;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface PaymentOperationStore extends Store<PaymentOperation, UUID>
{
}
