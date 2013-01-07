package org.mayocat.shop.store.provider;

import java.lang.reflect.Type;

import javax.inject.Provider;

import org.mayocat.shop.store.AbstractStoreProvider;
import org.mayocat.shop.store.CategoryStore;

public class CategoryStoreProvider extends AbstractStoreProvider<CategoryStore> implements Provider<CategoryStore>
{

    protected Type getType()
    {
        return CategoryStore.class;
    }
}

