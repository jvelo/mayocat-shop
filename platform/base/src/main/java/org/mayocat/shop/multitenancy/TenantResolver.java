package org.mayocat.shop.multitenancy;

import org.mayocat.shop.model.Tenant;
import org.xwiki.component.annotation.Role;

/**
 * Resolve which tenant correspond to a certain hostname.
 */
@Role
public interface TenantResolver
{
    /**
     * @param host the host to resolve the tenant for
     * @return the resolve tenant or null if no tenant is found for the passed host
     */
    Tenant resolve(String host);
}
