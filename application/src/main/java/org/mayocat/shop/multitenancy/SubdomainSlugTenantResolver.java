package org.mayocat.shop.multitenancy;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.mayocat.shop.configuration.MultitenancyConfiguration;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.service.AccountsService;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;
import com.google.common.net.InternetDomainName;

@Component("subdomain")
@Singleton
public class SubdomainSlugTenantResolver implements TenantResolver, ServletRequestListener
{

    /**
     * Request-scoped cache so that we don't call the store multiple times per request for the same host.
     * FIXME: maybe setup a global cache instead.
     */
    private ThreadLocal<Map<String, Tenant>> resolved = new ThreadLocal<Map<String, Tenant>>();

    @Inject
    private AccountsService accountsService;

    @Inject
    private Logger logger;

    @Inject
    private MultitenancyConfiguration configuration;

    public void requestDestroyed()
    {
        this.resolved.set(null);
        this.resolved.remove();
    }
    
    @Override
    public Tenant resolve(String host)
    {
        if (this.resolved.get() == null) {
            this.resolved.set(new HashMap<String, Tenant>());
        }
        if (!this.resolved.get().containsKey(host)) {
            Tenant tenant = null;
            try {
                if (!this.configuration.isActivated()) {
                    // Mono-tenant

                    tenant = this.accountsService.findTenant(this.configuration.getDefaultTenantSlug());
                    if (tenant == null) {
                        tenant = this.accountsService.createDefaultTenant();
                    }
                    this.resolved.get().put(host, tenant);
                } else {
                    // Multi-tenant

                    tenant = this.accountsService.findTenant(this.extractSlugFromHost(host));
                    if (tenant == null) {
                        return null;
                    }
                    this.resolved.get().put(host, tenant);
                }
            } catch (EntityAlreadyExistsException e) {
                // Has been created in between ?
                this.logger.warn("Failed attempt at creating a tenant that already exists for host {}", host);
            }
        }
        return this.resolved.get().get(host);
    }

    private String extractSlugFromHost(String host)
    {
        String rootDomain;
        if (Strings.emptyToNull(configuration.getRootDomain()) == null) {
            InternetDomainName domainName = InternetDomainName.from(host);
            if (domainName.hasPublicSuffix()) {
                // Domain is under a valid TLD, extract the TLD + first child
                rootDomain = domainName.topPrivateDomain().name();
            }
            else if (host.indexOf(".") > 0 && host.indexOf(".") < host.length()){
                // Otherwise, best guess : strip everything before the first dot.
                rootDomain = host.substring(host.indexOf(".") + 1);
            }
            else {
                rootDomain = host;
            }
        } else {
            rootDomain = configuration.getRootDomain();
        }
        if (host.indexOf("." + rootDomain) > 0) {
            return host.substring(0, host.indexOf("." + rootDomain));
        } else {
            return host;
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre)
    {
        this.resolved.remove();
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre)
    {
    }

}
