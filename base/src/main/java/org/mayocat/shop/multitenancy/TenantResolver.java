package org.mayocat.shop.multitenancy;

import org.mayocat.shop.model.Tenant;
import org.xwiki.component.annotation.Role;

@Role
public interface TenantResolver
{
    Tenant resolve(String host);
}
