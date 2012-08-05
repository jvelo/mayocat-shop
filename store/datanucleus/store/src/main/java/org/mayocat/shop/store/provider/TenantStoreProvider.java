package org.mayocat.shop.store.provider;

import java.lang.reflect.Type;

import javax.inject.Provider;

import org.mayocat.shop.store.AbstractStoreProvider;
import org.mayocat.shop.store.TenantStore;

public class TenantStoreProvider extends AbstractStoreProvider<TenantStore> implements Provider<TenantStore>
{

    protected Type getType()
    {
        return TenantStore.class;
    }

}
