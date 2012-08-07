package org.mayocat.shop.service.internal;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.model.Product;
import org.mayocat.shop.service.ProductService;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.ProductStore;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

@Component
public class DefaultProductService implements ProductService
{
    @Inject
    private Provider<ProductStore> productStore;

    public void create(Product entity) throws EntityAlreadyExistsException, StoreException
    {
        this.productStore.get().create(entity);
    }

    public void update(Product entity) throws StoreException
    {
        this.productStore.get().update(entity);
    }

    public Product findByHandle(String handle) throws StoreException
    {
        return this.productStore.get().findByHandle(handle);
    }

    public List<Product> findAll(int number, int offset) throws StoreException
    {
        // TODO
        throw new RuntimeException("Not implemented");
    }

}
