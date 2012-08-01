package org.mayocat.shop.store.datanucleus;

import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.jdo.JDOHelper;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.mayocat.shop.configuration.DataSourceConfiguration;
import org.mayocat.shop.store.EventListener;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

@Component
public class LifeCycle implements ServletRequestListener, EventListener
{

    @Inject
    private DataSourceConfiguration configuration;
    
    @Inject
    private PersistenceManagerProvider provider;
    
    @Override
    public void requestDestroyed(ServletRequestEvent sre)
    {
        if (this.provider.get() != null) {
            this.provider.get().close();
        }
        else {
            System.out.println("COULD not clean up threadlocal : not null");
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre)
    {
        Properties props = defaultProperties();
        
        for (Map.Entry<String, String> entry : this.configuration.getProperties().entrySet()) {
            props.setProperty(entry.getKey(), Strings.nullToEmpty(entry.getValue()));
        }
        
        this.provider.set(JDOHelper.getPersistenceManagerFactory(props).getPersistenceManager());
        System.out.println("FOUND pm : " + this.provider.get());
    }

    private Properties defaultProperties()
    {
        Properties props = new Properties();
        props.put("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
        props.put("datanucleus.autoCreateSchema", "true");
        props.put("datanucleus.validateTables", "false");
        props.put("datanucleus.validateConstraints", "true");
        props.put("datanucleus.identifier.case", "PreserveCase");

        // FIXME resolve tenant
        props.put("datanucleus.tenantId", "trololo");

        // Ensure field values are not unloaded when object are moved into hollow state.
        props.put("datanucleus.RetainValues", "true");

        return props;
    }

}
