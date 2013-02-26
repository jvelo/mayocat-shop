package org.mayocat.store;

import org.mayocat.model.Tenant;
import org.mayocat.model.TenantConfiguration;
import org.xwiki.component.annotation.Role;

@Role
public interface TenantStore extends Store<Tenant, Long>
{
    Tenant findBySlug(String slug);

    void updateConfiguration(TenantConfiguration configuration);
}
