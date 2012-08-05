package org.mayocat.shop.store.provider;

import java.lang.reflect.Type;

import javax.inject.Provider;

import org.mayocat.shop.store.AbstractStoreProvider;
import org.mayocat.shop.store.TenantStore;
import org.mayocat.shop.store.UserStore;

public class UserStoreProvider extends AbstractStoreProvider<UserStore> implements Provider<UserStore>
{

    @Override
    protected Type getType()
    {
        return UserStore.class;
    }

}
