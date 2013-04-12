package org.mayocat.shop.billing.store;

import org.mayocat.shop.billing.model.Customer;
import org.mayocat.store.EntityStore;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface CustomerStore extends Store<Customer, Long>, EntityStore
{
    Customer findBySlug(String slug);
}
