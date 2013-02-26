package org.mayocat.multitenancy;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.mayocat.base.EventListener;
import org.xwiki.component.annotation.Component;

@Component
@Named("subdomainSlugTenantResolverEventListener")
public class SubdomainSlugTenantResolverServletEventListener implements ServletRequestListener, EventListener
{
    @Inject
    @Named("subdomain")
    private TenantResolver defaultTenantResolver;

    @Override
    public void requestDestroyed(ServletRequestEvent sre)
    {
        try {
            SubdomainSlugTenantResolver dtr = (SubdomainSlugTenantResolver) this.defaultTenantResolver;
            if (dtr != null) {
                dtr.requestDestroyed();
            }
        } catch (ClassCastException e) {
            // This mean we are not using the sub-domain slug tenant resolver...
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre)
    {
    }
}
