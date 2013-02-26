package org.mayocat.shop.rest.resources;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import javax.jdo.JDOHelper;

import org.mayocat.shop.configuration.AuthenticationConfiguration;
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
    }

    private void registerConfigurationsAsComponents() throws Exception
    {
        for (Class<?> clazz : Arrays.<Class<?>> asList(
            MayocatShopConfiguration.class,
            DataSourceConfiguration.class,
            MultitenancyConfiguration.class,
            AuthenticationConfiguration.class
        )) {
            DefaultComponentDescriptor cd =
                new DefaultComponentDescriptor();
            cd.setRoleType(clazz);
            try {
                componentManager.registerComponent(cd, clazz.newInstance());
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
