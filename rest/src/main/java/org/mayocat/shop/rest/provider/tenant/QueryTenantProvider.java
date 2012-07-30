package org.mayocat.shop.rest.provider.tenant;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.rest.provider.Provider;
import org.xwiki.component.annotation.Component;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

@Component("queryTenant")
public class QueryTenantProvider implements InjectableProvider<QueryTenant, Parameter>, Provider
{

    @Override
    public Injectable<Tenant> getInjectable(ComponentContext componentContext, QueryTenant aannotation, Parameter parameter)
    {
        return new QueryTenantInjectable();
    }

    @Override
    public ComponentScope getScope()
    {
        return ComponentScope.PerRequest;
    }

}
