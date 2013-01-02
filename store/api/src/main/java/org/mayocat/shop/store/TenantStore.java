package org.mayocat.shop.store;

import org.mayocat.shop.model.Tenant;
import org.xwiki.component.annotation.Role;

@Role
public interface TenantStore extends Store<Tenant, Long>
{    
    Tenant findBySlug(String slug) throws StoreException;
    
    Tenant findBySlugOrAlias(String slugOrAlias) throws StoreException;
}
