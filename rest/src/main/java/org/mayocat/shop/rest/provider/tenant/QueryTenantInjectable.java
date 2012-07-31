package org.mayocat.shop.rest.provider.tenant;

import javax.inject.Provider;

import org.mayocat.shop.model.Tenant;

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
        return this.provider.get().resolve(context);
    }

}
