package org.mayocat.shop.store;

import org.mayocat.shop.model.Tenant;
import org.xwiki.component.annotation.Role;

@Role
public interface TenantStore extends Store<Tenant, Long>
{    
    Tenant findByHandle(String handle) throws StoreException;
    
    Tenant findByHandleOrAlias(String handleOrAlias) throws StoreException;
}
