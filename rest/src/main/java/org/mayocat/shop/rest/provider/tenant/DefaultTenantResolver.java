package org.mayocat.shop.rest.provider.tenant;

import org.mayocat.shop.model.Tenant;
import org.xwiki.component.annotation.Component;

import com.sun.jersey.api.core.HttpContext;

@Component
public class DefaultTenantResolver implements TenantResolver
{

    @Override
    public Tenant resolve(HttpContext context)
    {
        String host = context.getUriInfo().getBaseUri().getHost();
        return new Tenant(host);
    }

}
