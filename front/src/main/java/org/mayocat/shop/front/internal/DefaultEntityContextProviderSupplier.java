package org.mayocat.shop.front.internal;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.mayocat.shop.front.EntityContextProvider;
import org.mayocat.shop.front.EntityContextProviderSupplier;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
@Singleton
public class DefaultEntityContextProviderSupplier implements EntityContextProviderSupplier
{
    private Map<Class, EntityContextProvider<?>> providers = new HashMap<Class, EntityContextProvider<?>>();

    @Override
    public void registerProvider(Class clazz, EntityContextProvider<?> provider)
    {
        providers.put(clazz, provider);
    }

    @Override
    public boolean canSupply(Class clazz)
    {
        return providers.containsKey(clazz);
    }

    @Override
    public EntityContextProvider<?> supply(Class clazz)
    {
        if (canSupply(clazz)) {
            return providers.get(clazz);
        }
        return null;
    }
}
