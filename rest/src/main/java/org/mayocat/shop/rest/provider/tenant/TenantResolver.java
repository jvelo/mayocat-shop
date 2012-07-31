package org.mayocat.shop.rest.provider.tenant;

import org.mayocat.shop.model.Tenant;
import org.xwiki.component.annotation.Role;

import com.sun.jersey.api.core.HttpContext;

@Role
public interface TenantResolver
{
    Tenant resolve(HttpContext context);
}
