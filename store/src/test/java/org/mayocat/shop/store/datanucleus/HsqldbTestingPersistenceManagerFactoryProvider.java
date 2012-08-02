package org.mayocat.shop.store.datanucleus;

import java.util.Properties;

import javax.inject.Singleton;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.xwiki.component.annotation.Component;

@Component
@Singleton
public class HsqldbTestingPersistenceManagerFactoryProvider implements PersistenceManagerProvider
{
    private PersistenceManager pm;
    
    private PersistenceManagerFactory pmf;

    public HsqldbTestingPersistenceManagerFactoryProvider()
    {
        Properties props = defaultProperties();

        this.pmf = JDOHelper.getPersistenceManagerFactory(props);
    }

    public PersistenceManager get()
    {
        return pmf.getPersistenceManager();
    }

    private Properties defaultProperties()
    {
        Properties props = new Properties();
        props.put("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
        props.put("javax.jdo.option.ConnectionURL", "jdbc:hsqldb:mem:test");
        props.put("javax.jdo.option.ConnectionUserName", "sa");
        props.put("javax.jdo.option.ConnectionPassword", "");
        props.put("datanucleus.autoCreateSchema", "true");
        props.put("datanucleus.validateTables", "false");
        props.put("datanucleus.validateConstraints", "true");
        props.put("datanucleus.ConnectionDriverName", "org.hsqldb.jdbc.JDBCDriver");
        
        // Ensure field values are not unloaded when object are moved into hollow state.
        props.put("datanucleus.RetainValues", "true");
        
        return props;
    }

    @Override
    public void set(PersistenceManager pm)
    {
       this.pm = pm;
    }
}
