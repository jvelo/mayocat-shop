package org.mayocat.shop.multitenancy.jersey;

import javax.inject.Provider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.multitenancy.TenantResolver;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;


public class QueryTenantInjectable extends AbstractHttpContextInjectable<Tenant>
{

    private Provider<TenantResolver>  provider;
    
    public QueryTenantInjectable(Provider<TenantResolver> provider)
    {
        this.provider = provider;
    }

    @Override
    public Tenant getValue(HttpContext context)
    {
        Tenant t = this.provider.get().resolve(context.getUriInfo().getBaseUri().getHost());
        if (t == null) {
            // FIXME probably this check should be made in a com.sun.jersey.spi.container.ContainerRequestFilter
            // so that it dispenses resources from having the QueryTenant annotated parameter in their signature
            // since most of the times they will not be using it.
            // Need to check if/how such a filter can be registered against DropWizard with XWiki component injection
            // done properly.
            
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                .entity("No valid tenant found at this address.")
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build());
        }
        return t;
    }
}
