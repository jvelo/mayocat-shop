package org.mayocat.shop.authorization.jersey;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.shop.authorization.Authenticator;
import org.mayocat.shop.authorization.Gatekeeper;
import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.base.Provider;
import org.mayocat.shop.multitenancy.TenantResolver;
import org.mayocat.shop.service.UserService;
import org.xwiki.component.annotation.Component;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

@Component("authorized")
public class AuthorizedProvider implements InjectableProvider<Authorized, Parameter>, Provider
{
    @Inject
    private javax.inject.Provider<TenantResolver> tenantResolverProvider;

    @Inject
    private Map<String, Authenticator> authenticators;

    @Inject
    private Gatekeeper gatekeeper;

    @Inject
    private UserService userService;
    
    @Override
    public ComponentScope getScope()
    {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable< ? > getInjectable(ComponentContext ic, Authorized a, Parameter c)
    {
        return new AuthorizedInjectable(userService, tenantResolverProvider, authenticators, gatekeeper, a);
    }

}
