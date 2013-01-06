package org.mayocat.shop.multitenancy;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.mayocat.shop.base.EventListener;
import org.xwiki.component.annotation.Component;

@Component
@Named("tenantResolverEventListener")
public class DefaultTenantResolverServletEventListener implements ServletRequestListener, EventListener
{
    @Inject
    @Named("subdomain")
    private TenantResolver defaultTenantResolver;

    @Override
    public void requestDestroyed(ServletRequestEvent sre)
    {
        DefaultTenantResolver dtr = (DefaultTenantResolver) this.defaultTenantResolver;
        if (dtr != null) {
            dtr.requestDestroyed();
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre)
    {
    }

}
