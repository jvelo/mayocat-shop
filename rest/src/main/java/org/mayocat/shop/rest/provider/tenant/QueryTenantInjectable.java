package org.mayocat.shop.rest.provider.tenant;

import org.mayocat.shop.model.Tenant;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;

public class QueryTenantInjectable extends AbstractHttpContextInjectable<Tenant>
{

    @Override
    public Tenant getValue(HttpContext context)
    {
        String host = context.getUriInfo().getBaseUri().getHost();
        return new Tenant(host);
    }

}
