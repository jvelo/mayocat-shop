package org.mayocat.accounts.store;

import java.util.UUID;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.TenantConfiguration;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

@Role
public interface TenantStore extends Store<Tenant, UUID>
{
    Tenant findBySlug(String slug);

    Tenant findByDefaultHost(String host);

    void updateConfiguration(TenantConfiguration configuration);
}
