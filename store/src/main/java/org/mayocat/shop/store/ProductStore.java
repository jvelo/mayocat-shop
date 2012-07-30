package org.mayocat.shop.store;

import javax.validation.Valid;

import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.xwiki.component.annotation.Role;

@Role
public interface ProductStore
{
    void create(@Valid Product p) throws StoreException;
    
    void update(@Valid Product p) throws StoreException;
    
    Product findById(Long id) throws StoreException;
    
    Product findByTenantAndHandle(Tenant tenant, String handle) throws StoreException;
}
