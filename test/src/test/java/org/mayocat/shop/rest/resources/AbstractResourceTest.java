package org.mayocat.shop.rest.resources;

import java.util.Map;
import java.util.Properties;

import javax.jdo.JDOHelper;

import org.mayocat.shop.configuration.DataSourceConfiguration;
import org.mayocat.shop.configuration.MayocatShopConfiguration;
import org.mayocat.shop.configuration.MultitenancyConfiguration;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.TenantStore;
import org.mayocat.shop.store.datanucleus.PersistenceManagerProvider;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.embed.EmbeddableComponentManager;

import com.yammer.dropwizard.testing.ResourceTest;

public abstract class AbstractResourceTest extends ResourceTest
{

    private PersistenceManagerProvider provider;

    protected EmbeddableComponentManager componentManager;

    private TenantStore tenantStore;

    public AbstractResourceTest()
    {
        super();
    }

    @Override
    protected void setUpResources() throws Exception
    {
        // Initialize Rendering components and allow getting instances
        componentManager = new EmbeddableComponentManager();
        componentManager.initialize(this.getClass().getClassLoader());

        this.registerConfigurationsAsComponents();

        // Registering provider component implementations against the test
        // environment...
        Map<String, org.mayocat.shop.base.Provider> providers =
            componentManager.getInstanceMap(org.mayocat.shop.base.Provider.class);
        for (Map.Entry<String, org.mayocat.shop.base.Provider> provider : providers.entrySet()) {
            this.addResource(provider.getValue());
        }

        this.provider = this.componentManager.getInstance(PersistenceManagerProvider.class);

        this.setUpPmf();


    }

    private void setUpPmf() throws Exception
    {
        Properties props = getPersistenceProperties();
        this.provider.set(JDOHelper.getPersistenceManagerFactory(props).getPersistenceManager());

        this.tenantStore = this.componentManager.getInstance(TenantStore.class);
        if (this.tenantStore.findByHandle("default") == null) {
            this.tenantStore.create(new Tenant("default"));
        }

        props.put("datanucleus.tenantId", "testing");
        this.provider.set(JDOHelper.getPersistenceManagerFactory(props).getPersistenceManager());
    }

    private Properties getPersistenceProperties()
    {
        Properties props = new Properties();
        props.put("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
        props.put("datanucleus.autoCreateSchema", "true");
        props.put("datanucleus.validateTables", "false");
        props.put("datanucleus.validateConstraints", "true");
        props.put("datanucleus.identifier.case", "PreserveCase");
        props.put("javax.jdo.option.ConnectionDriverName", "org.hsqldb.jdbc.JDBCDriver");
        props.put("javax.jdo.option.ConnectionURL", "jdbc:hsqldb:mem:mayocat");
        props.put("javax.jdo.option.ConnectionUserName", "sa");
        props.put("javax.jdo.option.ConnectionPassword", "");

        // Ensure field values are not unloaded when object are moved into
        // hollow state.
        props.put("datanucleus.RetainValues", "true");

        return props;
    }

    private void registerConfigurationsAsComponents()
    {
        DefaultComponentDescriptor<MayocatShopConfiguration> cd =
            new DefaultComponentDescriptor<MayocatShopConfiguration>();
        cd.setRoleType(MayocatShopConfiguration.class);
        componentManager.registerComponent(cd, new MayocatShopConfiguration());

        DefaultComponentDescriptor<DataSourceConfiguration> cd2 =
            new DefaultComponentDescriptor<DataSourceConfiguration>();
        cd2.setRoleType(DataSourceConfiguration.class);
        componentManager.registerComponent(cd2, new DataSourceConfiguration());

        DefaultComponentDescriptor<MultitenancyConfiguration> cd3 =
            new DefaultComponentDescriptor<MultitenancyConfiguration>();
        cd3.setRoleType(MultitenancyConfiguration.class);
        componentManager.registerComponent(cd3, new MultitenancyConfiguration());
    }

}
