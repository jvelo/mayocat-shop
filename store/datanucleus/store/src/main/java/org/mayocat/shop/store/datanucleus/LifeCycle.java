package org.mayocat.shop.store.datanucleus;

import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.mayocat.shop.base.EventListener;
import org.mayocat.shop.configuration.DataSourceConfiguration;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.multitenancy.TenantResolver;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

@Component
public class LifeCycle implements ServletRequestListener, EventListener
{

    @Inject
    private Provider<TenantResolver> tenantResolverProdiver;

    @Inject
    private DataSourceConfiguration configuration;

    @Inject
    private PersistenceManagerProvider provider;

    @Inject
    private Logger logger;

    @Override
    public void requestDestroyed(ServletRequestEvent sre)
    {
        if (this.provider.get() != null) {
            this.provider.get().close();
        } else {
            this.logger.debug("No persistence manager to close for request {}",
                ((HttpServletRequest) sre.getServletRequest()).getPathInfo());
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent event)
    {
        HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
        if (request.getRequestURI().startsWith("/admin/")) {
            // When serving static assets, do not initialize a persistence manager
            // TODO have the "/admin/" path configured in am application constant
            return;
        }

        // Step 1. Resolve tenant
        Properties props = getPersistenceProperties();

        PersistenceManager tenantAgnosticPersistenceManager =
            JDOHelper.getPersistenceManagerFactory(props).getPersistenceManager();
        this.provider.set(tenantAgnosticPersistenceManager);

        String host = event.getServletRequest().getServerName();
        Tenant tenant = this.tenantResolverProdiver.get().resolve(host);

        if (tenant == null) {
            // Leave it to Jersey resource to throw the appropriate exception...
            return;
        }

        // Release agnostic PM
        this.provider.get().close();
        this.provider.set(null);

        // Step 2. Set request persistence manager with proper tenant ID.
        props = getPersistenceProperties();

        props.put("datanucleus.tenantId", tenant.getHandle());

        this.provider.set(JDOHelper.getPersistenceManagerFactory(props).getPersistenceManager());

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Persistence manager {} set for request with tenant {}", this.provider.get(), tenant);
        }
    }

    private Properties getPersistenceProperties()
    {
        Properties props = new Properties();
        props.put("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
        props.put("datanucleus.autoCreateSchema", "true");
        props.put("datanucleus.validateTables", "false");
        props.put("datanucleus.validateConstraints", "true");
        props.put("datanucleus.identifier.case", "PreserveCase");

        // Ensure field values are not unloaded when object are moved into hollow state.
        props.put("datanucleus.RetainValues", "true");

        // Override/extend with configuration
        for (Map.Entry<String, String> entry : this.configuration.getProperties().entrySet()) {
            props.setProperty(entry.getKey(), Strings.nullToEmpty(entry.getValue()));
        }

        return props;
    }

}
