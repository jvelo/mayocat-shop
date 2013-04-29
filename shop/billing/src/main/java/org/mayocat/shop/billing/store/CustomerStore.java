package org.mayocat.shop.billing.store;

import java.util.UUID;

import org.mayocat.shop.billing.model.Customer;
import org.mayocat.store.EntityStore;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface CustomerStore extends Store<Customer, UUID>, EntityStore
{
    Customer findBySlug(String slug);
}
