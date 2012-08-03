package org.mayocat.shop.store.datanucleus;

import java.util.Properties;

import javax.jdo.JDOHelper;

import org.junit.Before;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.test.AbstractMockingComponentTestCase;

public abstract class AbstractStoreEntityTestCase extends AbstractMockingComponentTestCase
{

    protected PersistenceManagerProvider provider = new DefaultPersistenceManagerProdiver();

    private String tenant = "default";
    
    @Before
    public void setUpStore()
    {
        Properties props = defaultProperties();
        this.provider.set(JDOHelper.getPersistenceManagerFactory(props).getPersistenceManager());
    }

    /**
     * Setup mock dependencies before initializing the @MockingRequirement components.
     */
    @Override
    protected void setupDependencies() throws Exception
    {
        DefaultComponentDescriptor<PersistenceManagerProvider> cd =
            new DefaultComponentDescriptor<PersistenceManagerProvider>();
        cd.setRoleType(PersistenceManagerProvider.class);
        this.getComponentManager().registerComponent(cd, this.provider);
    }
    
    public void setTenantToResolveTo(String tenant)
    {
        this.tenant = tenant;
    }
    
    public void setUpPersistenceManager() throws Exception
    {
        this.setUpStore();
        this.setupDependencies();
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

        props.put("datanucleus.tenantId", this.tenant);

        return props;
    }
}
