package org.mayocat.shop.store.provider;

import java.lang.reflect.Type;

import javax.inject.Provider;

import org.mayocat.shop.store.AbstractStoreProvider;
import org.mayocat.shop.store.UserRoleStore;
import org.mayocat.shop.store.UserStore;

public class UserRoleStoreProvider extends AbstractStoreProvider<UserStore> implements Provider<UserStore>
{

    protected Type getType()
    {
        return UserRoleStore.class;
    }

}
