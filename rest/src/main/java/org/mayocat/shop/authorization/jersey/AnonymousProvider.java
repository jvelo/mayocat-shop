package org.mayocat.shop.authorization.jersey;

import javax.inject.Inject;

import org.mayocat.shop.authorization.annotation.Anonymous;
import org.mayocat.shop.base.Provider;
import org.mayocat.shop.context.Execution;
import org.mayocat.shop.multitenancy.TenantResolver;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

public class AnonymousProvider implements InjectableProvider<Anonymous, Parameter>, Provider
{

    @Inject
    private javax.inject.Provider<TenantResolver> tenantResolverProvider;
    
    @Inject
    private Execution execution;
    
    @Override
    public ComponentScope getScope()
    {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable< ? > getInjectable(ComponentContext ic, Anonymous a, Parameter c)
    {
        return new AnonymousInjectable(tenantResolverProvider, execution);
    }

}
