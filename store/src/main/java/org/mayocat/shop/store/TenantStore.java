package org.mayocat.shop.store;

import javax.validation.Valid;

import org.mayocat.shop.model.Tenant;
import org.xwiki.component.annotation.Role;

@Role
public interface TenantStore extends Store<Tenant>
{
    void create(@Valid Tenant t) throws StoreException;
    
    void update(@Valid Tenant t) throws StoreException;
    
    Tenant findById(Long id) throws StoreException;
    
    Tenant findByHandle(String handle) throws StoreException;
    
    Tenant findByHandleOrAlias(String handleOrAlias) throws StoreException;
}
