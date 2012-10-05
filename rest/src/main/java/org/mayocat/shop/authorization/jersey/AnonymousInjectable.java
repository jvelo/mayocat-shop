package org.mayocat.shop.authorization.jersey;

import javax.inject.Provider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.shop.context.Context;
import org.mayocat.shop.context.Execution;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.multitenancy.TenantResolver;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;

public class AnonymousInjectable extends AbstractHttpContextInjectable<Context>
{

    protected Execution execution;

    protected Provider<TenantResolver> provider;

    public AnonymousInjectable(Provider<TenantResolver> provider, Execution execution)
    {
        this.provider = provider;
        this.execution = execution;
    }

    @Override
    public Context getValue(HttpContext httpContext)
    {
        Tenant tenant = this.provider.get().resolve(httpContext.getUriInfo().getBaseUri().getHost());
        if (tenant == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                .entity("No valid tenant found at this address.").type(MediaType.TEXT_PLAIN_TYPE).build());
        }
        Context context = new Context(tenant, null);
        this.execution.setContext(context);
        return context;
    }

}
