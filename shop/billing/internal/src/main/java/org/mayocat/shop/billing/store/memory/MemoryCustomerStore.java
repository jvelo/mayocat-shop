package org.mayocat.shop.billing.store.memory;

import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.billing.store.CustomerStore;
import org.mayocat.store.memory.BaseEntityMemoryStore;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.FluentIterable;

/**
 * In-memory implementation of {@link CustomerStore}
 *
 * @version $Id$
 */
@Component("memory")
public class MemoryCustomerStore extends BaseEntityMemoryStore<Customer> implements CustomerStore
{
    public Customer findBySlug(String slug)
    {
        return FluentIterable.from(all()).filter(withSlug(slug)).first().orNull();
    }
}
