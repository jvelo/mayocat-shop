package org.mayocat.shop.store.datanucleus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.datanucleus.NucleusContext;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.store.schema.SchemaAwareStoreManager;
import org.mayocat.shop.base.EventListener;
import org.mayocat.shop.configuration.DataSourceConfiguration;
import org.mayocat.shop.event.ApplicationStartedEvent;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.multitenancy.TenantResolver;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.observation.ObservationManager;
import org.xwiki.observation.event.Event;

import com.google.common.base.Strings;

@Singleton
@Component
public class LifeCycle implements ServletRequestListener, EventListener, Initializable
{

    /**
     * FIXME this certainly shouldn't be here.
     */
    private static final String DEFAULT_CLIENT_STATIC_PATH = "/admin/";

    @Inject
    private Provider<TenantResolver> tenantResolverProdiver;

    @Inject
    private DataSourceConfiguration configuration;

    @Inject
    private PersistenceManagerProvider provider;

    @Inject
    private ObservationManager observationManager;

    @Inject
    private Logger logger;

    private Map<String, PersistenceManagerFactory> factories = new HashMap<String, PersistenceManagerFactory>();

    private PersistenceManagerFactory tenantAgnosticFactory = null;

    private class EventListener implements org.xwiki.observation.EventListener
    {

        @Override
        public String getName()
        {
            return "datanucleusPersistenceManagerLifecycle";
        }

        @Override
        public List<Event> getEvents()
        {
            return Arrays.<Event> asList(new ApplicationStartedEvent());
        }

        @Override
        public void onEvent(Event event, Object source, Object data)
        {
            createAllSchemas();
        }

    }

    private void createAllSchemas()
    {

    }

    @Override
    public void initialize() throws InitializationException
    {
        this.observationManager.addListener(new EventListener());
    }

    @Override
    public void requestDestroyed(ServletRequestEvent event)
    {
        if (this.provider.get() != null) {
            this.provider.get().close();
            this.provider.set(null);
        } else {
            if (this.logger.isDebugEnabled()
                && !((HttpServletRequest) event.getServletRequest()).getRequestURI().startsWith(
                    DEFAULT_CLIENT_STATIC_PATH)) {
                this.logger.debug("No persistence manager to close for request {}",
                    ((HttpServletRequest) event.getServletRequest()).getPathInfo());
            }
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent event)
    {
        HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
        if (request.getRequestURI().startsWith(DEFAULT_CLIENT_STATIC_PATH)) {
            // When serving static assets, do not initialize a persistence manager
            // TODO have the "/admin/" path configured in am application constant
            return;
        }

        // Step 1. Resolve tenant
        if (this.tenantAgnosticFactory == null) {
            tenantAgnosticFactory = JDOHelper.getPersistenceManagerFactory(getPersistenceProperties());
        }
        this.provider.set(tenantAgnosticFactory.getPersistenceManager());

        String host = event.getServletRequest().getServerName();
        Tenant tenant = this.tenantResolverProdiver.get().resolve(host);

        if (tenant == null) {
            // Leave it to Jersey resource to throw the appropriate exception...
            return;
        }

        // Release agnostic PM
        this.provider.get().close();
        this.provider.set(null);

        if (!this.factories.containsKey(tenant.getSlug())) {
            Properties props = getPersistenceProperties();
            props.put("datanucleus.tenantId", tenant.getSlug());
            this.factories.put(tenant.getSlug(), JDOHelper.getPersistenceManagerFactory(props));
        }
        // Step 2. Set request persistence manager with proper tenant ID.
        this.provider.set(this.factories.get(tenant.getSlug()).getPersistenceManager());

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Persistence manager {} set for request with tenant {}", this.provider.get(), tenant);
        }
    }

    private Properties getPersistenceProperties()
    {
        Properties props = new Properties();
        props.put("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
        props.put("datanucleus.autoCreateWarnOnError", "false");
        props.put("datanucleus.validateTables", "false");
        props.put("datanucleus.validateConstraints", "true");
        props.put("datanucleus.identifier.case", "PreserveCase");

        // Ensure schema are not automatically created. This causes issues when performed in a non multi-tenant context
        // (i.e. a PMF created without the "datanucleus.tenantId" property set, because some constraints need the tenant
        // column to be present.
        // Instead, we ensure automatic schema creation at startup of the application, programmatically.
        props.put("datanucleus.autoCreateSchema", "false");
        
        // Ensure field values are not unloaded when object are moved into hollow state.
        props.put("datanucleus.RetainValues", "true");

        // Override/extend with configuration
        for (Map.Entry<String, String> entry : this.configuration.getProperties().entrySet()) {
            props.setProperty(entry.getKey(), Strings.nullToEmpty(entry.getValue()));
        }

        return props;
    }

}
