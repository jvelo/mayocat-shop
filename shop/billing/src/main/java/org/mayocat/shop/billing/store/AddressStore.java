package org.mayocat.shop.billing.store;

import org.mayocat.shop.billing.model.Address;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface AddressStore extends Store<Address, Long>
{
}
