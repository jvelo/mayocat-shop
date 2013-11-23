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
