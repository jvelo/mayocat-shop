package org.mayocat.shop.authorization.jersey;

import javax.inject.Provider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.shop.authorization.Context;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.multitenancy.TenantResolver;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;

public class AnonymousInjectable extends AbstractHttpContextInjectable<Context>
{

    protected Provider<TenantResolver> provider;

    public AnonymousInjectable(Provider<TenantResolver> provider)
    {
        this.provider = provider;
    }

    @Override
    public Context getValue(HttpContext context)
    {
        Tenant tenant = this.provider.get().resolve(context.getUriInfo().getBaseUri().getHost());
        if (tenant == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                .entity("No valid tenant found at this address.").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
        return new Context(tenant, null);
    }

}
