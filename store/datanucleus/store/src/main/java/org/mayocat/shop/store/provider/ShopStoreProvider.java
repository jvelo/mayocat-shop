package org.mayocat.shop.store.provider;

import java.lang.reflect.Type;

import javax.inject.Provider;

import org.mayocat.shop.store.AbstractStoreProvider;
import org.mayocat.shop.store.ShopStore;
import org.xwiki.component.annotation.Component;

@Component
public class ShopStoreProvider  extends AbstractStoreProvider<ShopStore> implements Provider<ShopStore>
{

    protected Type getType()
    {
        return ShopStore.class;
    }

}
