package org.mayocat.shop.front;

import org.mayocat.shop.model.Entity;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface EntityContextProviderSupplier
{
    void registerProvider(Class clazz, EntityContextProvider<?> provider);

    boolean canSupply(Class clazz);

    EntityContextProvider<?> supply(Class clazz);
}
