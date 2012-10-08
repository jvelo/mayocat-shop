package org.mayocat.shop.store.datanucleus;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.datanucleus.NucleusContext;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.store.schema.SchemaAwareStoreManager;
import org.jmock.Expectations;
import org.junit.Before;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.observation.ObservationManager;
import org.xwiki.observation.event.Event;
import org.xwiki.test.AbstractMockingComponentTestCase;

public abstract class AbstractStoreEntityTestCase extends AbstractMockingComponentTestCase
{

    protected PersistenceManagerProvider provider = new DefaultPersistenceManagerProdiver();

    protected ObservationManager observationManager;

    private String tenant = "default";

    @Before
    public void setUpRequirements() throws ComponentLookupException, Exception
    {
        this.observationManager = this.getComponentManager().getInstance(ObservationManager.class);
        getMockery().checking(new Expectations()
        {
            {
                allowing(observationManager).notify(with(any(Event.class)), with(any(Object.class)),
                    with(any(Object.class)));
            }
        });
    }

    @Before
    public void setUpStore()
    {
        Properties props = defaultProperties();
        JDOPersistenceManagerFactory pmf = (JDOPersistenceManagerFactory) JDOHelper.getPersistenceManagerFactory(props);
        this.provider.set(pmf.getPersistenceManager());

        Set<String> classNames = new HashSet<String>();

        // FIXME Refactor this so that we don't have to list all entity classes.
        classNames.add("org.mayocat.shop.model.Category");
        classNames.add("org.mayocat.shop.model.User");
        classNames.add("org.mayocat.shop.model.Product");
        classNames.add("org.mayocat.shop.model.Role");
        classNames.add("org.mayocat.shop.model.Shop");
        classNames.add("org.mayocat.shop.model.Tenant");
        classNames.add("org.mayocat.shop.model.UserRole");
        NucleusContext ctx = pmf.getNucleusContext();

        Properties properties = new Properties();
        // Set any properties for schema generation
        ((SchemaAwareStoreManager) ctx.getStoreManager()).createSchema(classNames, properties);
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
        Properties props = defaultProperties();
        this.provider.set(JDOHelper.getPersistenceManagerFactory(props).getPersistenceManager());
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
