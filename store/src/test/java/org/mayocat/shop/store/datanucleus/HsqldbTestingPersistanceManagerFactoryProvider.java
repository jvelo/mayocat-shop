package org.mayocat.shop.store.datanucleus;

import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public class HsqldbTestingPersistanceManagerFactoryProvider implements PersistanceManagerFactoryProdiver
{
    private PersistenceManagerFactory pmf;

    public HsqldbTestingPersistanceManagerFactoryProvider()
    {
        Properties props = defaultProperties();

        this.pmf = JDOHelper.getPersistenceManagerFactory(props);
    }

    public PersistenceManagerFactory get()
    {
        return pmf;
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
        
        return props;
    }
}
