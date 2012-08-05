package org.mayocat.shop.store.provider;

import java.lang.reflect.Type;

import javax.inject.Provider;

import org.mayocat.shop.store.AbstractStoreProvider;
import org.mayocat.shop.store.RoleStore;
import org.xwiki.component.annotation.Component;

@Component
public class RoleStoreProvider extends AbstractStoreProvider<RoleStore> implements Provider<RoleStore>
{
    protected Type getType()
    {
        return RoleStore.class;
    }

}
