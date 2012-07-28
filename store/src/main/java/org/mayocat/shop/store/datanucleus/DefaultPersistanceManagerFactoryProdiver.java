package org.mayocat.shop.store.datanucleus;

import java.util.Properties;

import javax.inject.Inject;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.mayocat.shop.configuration.DataSourceConfiguration;
import org.xwiki.component.annotation.Component;

@Component
public class DefaultPersistanceManagerFactoryProdiver implements PersistanceManagerFactoryProdiver
{
    
    @Inject
    private DataSourceConfiguration configuration;
    
    private static PersistenceManagerFactory pmfInstance;

    public PersistenceManagerFactory get()
    {
        if (pmfInstance == null) {
            Properties props = defaultProperties();
            pmfInstance = JDOHelper.getPersistenceManagerFactory(props);
        }
        return pmfInstance;
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
        props.put("datanucleus.validateConstraints", "false");
        props.put("datanucleus.ConnectionDriverName", "org.hsqldb.jdbc.JDBCDriver");

        return props;
    }

}
