package org.mayocat.shop.multitenancy;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.shop.configuration.MultitenancyConfiguration;
import org.mayocat.shop.model.Shop;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.service.TenantService;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;
import com.google.common.net.InternetDomainName;

@Component
public class DefaultTenantResolver implements TenantResolver
{

    /**
     * Request-scoped cache so that we don't call the store multiple times per request for the same host.
     * FIXME: maybe setup a global cache instead.
     */
    private ThreadLocal<Map<String, Tenant>> resolved = new ThreadLocal<Map<String, Tenant>>();

    @Inject
    private TenantService tenantService;

    @Inject
    private Logger logger;

    @Inject
    private MultitenancyConfiguration configuration;

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
                    
                    tenant = this.tenantService.findBySlug(this.configuration.getDefaultTenant());
                    if (tenant == null) {
                        Tenant tenantToCreate = new Tenant(this.configuration.getDefaultTenant());
                        tenantToCreate.setShop(new Shop());
                        this.tenantService.create(tenantToCreate);
                        tenant = this.tenantService.findBySlug(this.configuration.getDefaultTenant());
                    }
                    this.resolved.get().put(host, tenant);
                } else {
                    // Multi-tenant
                    
                    tenant = this.tenantService.findBySlug(this.extractSlugFromHost(host));
                    if (tenant == null) {
                        return null;
                    }
                    this.resolved.get().put(host, tenant);
                }
            } catch (StoreException e) {
                this.logger.error("Error trying to resolve tenant for host {} : {}", host, e.getMessage());
            } catch (InvalidEntityException e) {
                throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
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

}
