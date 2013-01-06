package org.mayocat.shop.store;

import org.mayocat.shop.model.Tenant;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.xwiki.component.annotation.Role;

@Role
public interface TenantStore extends Store<Tenant, Long>
{    
    
    Tenant findBySlug(@Bind("slug") String slug) throws StoreException;
}
