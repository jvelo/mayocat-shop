package org.mayocat.shop.store.datanucleus;

import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.mayocat.shop.base.EventListener;
import org.mayocat.shop.configuration.DataSourceConfiguration;
import org.mayocat.shop.multitenancy.TenantResolver;
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

    @Override
    public void requestDestroyed(ServletRequestEvent sre)
    {
        if (this.provider.get() != null) {
            this.provider.get().close();
        } else {
            System.out.println("COULD not clean up threadlocal : not null");
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre)
    {
        // Step 1. Resolve tenant
        Properties props = getPersistenceProperties();

        PersistenceManager tenantAgnosticPersistenceManager =
            JDOHelper.getPersistenceManagerFactory(props).getPersistenceManager();
        this.provider.set(tenantAgnosticPersistenceManager);
        
        String host = sre.getServletRequest().getServerName();
        String tenant = this.tenantResolverProdiver.get().resolve(host).getHandle();

        // Release agnostic PM
        this.provider.get().close();
        this.provider.set(null);

        // Step 2. Set request persistence manager with proper tenant ID.
        props = getPersistenceProperties();

        props.put("datanucleus.tenantId", tenant);

        this.provider.set(JDOHelper.getPersistenceManagerFactory(props).getPersistenceManager());
        System.out.println("FOUND pm : " + this.provider.get());
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
