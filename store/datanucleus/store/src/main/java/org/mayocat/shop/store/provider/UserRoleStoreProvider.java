package org.mayocat.shop.store.provider;

import java.lang.reflect.Type;

import javax.inject.Provider;

import org.mayocat.shop.store.AbstractStoreProvider;
import org.mayocat.shop.store.UserRoleStore;

public class UserRoleStoreProvider extends AbstractStoreProvider<UserRoleStore> implements Provider<UserRoleStore>
{

    protected Type getType()
    {
        return UserRoleStore.class;
    }

}
