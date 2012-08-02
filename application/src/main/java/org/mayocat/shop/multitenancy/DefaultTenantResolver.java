package org.mayocat.shop.multitenancy;

import javax.inject.Inject;

import org.mayocat.shop.configuration.MultitenancyConfiguration;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.TenantStore;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;
import com.google.common.net.InternetDomainName;

@Component
public class DefaultTenantResolver implements TenantResolver
{

    /**
     * ThreadLocal for storing the resolved tenant for the current thread (request) so that we don't query the store
     * each time.
     */
    private ThreadLocal<Tenant> resolved = new ThreadLocal<Tenant>();

    @Inject
    private TenantStore tenantStore;

    @Inject
    private Logger logger;

    @Inject
    private MultitenancyConfiguration configuration;

    @Override
    public Tenant resolve(String host)
    {
        if (this.resolved.get() == null) {
            Tenant tenant = null;
            try {
                if (!this.configuration.isActivated()) {
                    tenant = this.tenantStore.findByHandle(this.configuration.getDefaultTenant());
                    if (tenant == null) {
                        this.tenantStore.create(new Tenant(this.configuration.getDefaultTenant()));
                        tenant = this.tenantStore.findByHandle(this.configuration.getDefaultTenant());
                    }
                    this.resolved.set(tenant);
                } else {
                    tenant = this.tenantStore.findByHandle(this.extractHandleFromHost(host));
                    if (tenant == null) {
                        return null;
                    }
                    this.resolved.set(tenant);
                }
            } catch (StoreException e) {
                this.logger.error("Error trying to resolve tenant for host {} : {}", host, e.getMessage());
            }
        }
        return this.resolved.get();
    }

    private String extractHandleFromHost(String host)
    {
        String rootDomain;
        if (Strings.emptyToNull(configuration.getRootDomain()) == null) {
            InternetDomainName domainName = InternetDomainName.from(host);
            return domainName.topPrivateDomain().name();
        } else {
            rootDomain = configuration.getRootDomain();
        }
        if (host.indexOf("." + rootDomain) > 0) {
            return host.substring((host.indexOf("." + rootDomain)));
        } else {
            return host;
        }
    }

}
