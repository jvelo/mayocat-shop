package org.mayocat.shop.store.provider;

import java.lang.reflect.Type;

import javax.inject.Provider;

import org.mayocat.shop.store.AbstractStoreProvider;
import org.mayocat.shop.store.ProductStore;
import org.xwiki.component.annotation.Component;

@Component
public class ProductStoreProvider extends AbstractStoreProvider<ProductStore> implements Provider<ProductStore>
{

    @Override
    protected Type getType()
    {
        return ProductStore.class;
    }

}
