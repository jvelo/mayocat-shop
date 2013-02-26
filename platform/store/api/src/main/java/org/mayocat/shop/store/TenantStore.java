package org.mayocat.shop.store;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.TenantConfiguration;
import org.xwiki.component.annotation.Role;

@Role
public interface TenantStore extends Store<Tenant, Long>
{
    Tenant findBySlug(String slug);

    void updateConfiguration(TenantConfiguration configuration);
}
