package org.mayocat.shop.application;

import java.lang.reflect.Field;
import java.util.Map;

import org.mayocat.shop.base.EventListener;
import org.mayocat.shop.base.HealthCheck;
import org.mayocat.shop.base.Managed;
import org.mayocat.shop.base.Provider;
import org.mayocat.shop.base.Task;
import org.mayocat.shop.configuration.MayocatShopConfiguration;
import org.mayocat.shop.event.ApplicationStartedEvent;
import org.mayocat.shop.rest.resources.Resource;
import org.mayocat.shop.store.rdbms.dbi.DBIProvider;
import org.skife.jdbi.v2.DBI;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.observation.ObservationManager;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import com.yammer.dropwizard.migrations.MigrationsBundle;

public class MayocatShopService extends Service<MayocatShopConfiguration>
{
    private EmbeddableComponentManager componentManager;

    public static void main(String[] args) throws Exception
    {
        new MayocatShopService().run(args);
    }

    @Override
    public void initialize(Bootstrap<MayocatShopConfiguration> bootstrap)
    {
        bootstrap.addBundle(new AssetsBundle("/client/", "/admin/"));
        bootstrap.addBundle(new DBIExceptionsBundle());
        bootstrap.addBundle(new MigrationsBundle<MayocatShopConfiguration>()
        {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(MayocatShopConfiguration configuration)
            {
                return configuration.getDatabaseConfiguration();
            }
        });
    }

    @Override
    public void run(MayocatShopConfiguration configuration, Environment environment) throws Exception
    {

        // Initialize Rendering components and allow getting instances
        componentManager = new EmbeddableComponentManager();

        this.registerConfigurationsAsComponents(configuration);
        this.registerDBIFactoryComponent(environment, configuration);

        componentManager.initialize(this.getClass().getClassLoader());

        // Registering provider component implementations against the environment...
        Map<String, Resource> providers = componentManager.getInstanceMap(Provider.class);
        for (Map.Entry<String, Resource> provider : providers.entrySet()) {
            environment.addProvider(provider.getValue());
        }

        // Registering resources component implementations against the environment...
        Map<String, Resource> restResources = componentManager.getInstanceMap(Resource.class);
        for (Map.Entry<String, Resource> resource : restResources.entrySet()) {
            environment.addResource(resource.getValue());
        }

        // Registering revent listeners implementations against the environment
        Map<String, EventListener> eventListeners = componentManager.getInstanceMap(EventListener.class);
        for (Map.Entry<String, EventListener> listener : eventListeners.entrySet()) {
            environment.addServletListeners(listener.getValue());
        }

        // Registering health checks implementations against the environment
        Map<String, HealthCheck> healthChecks = componentManager.getInstanceMap(HealthCheck.class);
        for (Map.Entry<String, HealthCheck> check : healthChecks.entrySet()) {
            if (com.yammer.metrics.core.HealthCheck.class.isAssignableFrom(check.getValue().getClass())) {
                environment.addHealthCheck((com.yammer.metrics.core.HealthCheck) check.getValue());
            }
        }

        // Registering tasks implementations against the environment
        Map<String, Task> tasks = componentManager.getInstanceMap(Task.class);
        for (Map.Entry<String, Task> task : tasks.entrySet()) {
            if (com.yammer.dropwizard.tasks.Task.class.isAssignableFrom(task.getValue().getClass())) {
                environment.addTask((com.yammer.dropwizard.tasks.Task) task.getValue());
            }
        }

        // Managed services that show a managed lifecycle
        Map<String, Managed> managedServices = componentManager.getInstanceMap(Managed.class);
        for (Map.Entry<String, Managed> managed : managedServices.entrySet()) {
            environment.manage(managed.getValue());
        }

        ObservationManager observationManager = componentManager.getInstance(ObservationManager.class);
        observationManager.notify(new ApplicationStartedEvent(), this);
    }

    private void registerDBIFactoryComponent(Environment environment, MayocatShopConfiguration configuration)
        throws ClassNotFoundException
    {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi =
            factory.build(environment, configuration.getDatabaseConfiguration(), configuration
                .getDataSourceConfiguration().getName());
        final DBIProvider dbi = new DBIProvider()
        {
            @Override
            public DBI get()
            {
                return jdbi;
            }
        };
        DefaultComponentDescriptor<DBIProvider> cd = new DefaultComponentDescriptor<DBIProvider>();
        cd.setRoleType(DBIProvider.class);
        componentManager.registerComponent(cd, dbi);
    }

    private void registerConfigurationsAsComponents(MayocatShopConfiguration configuration)
    {
        Field[] configurationFields = MayocatShopConfiguration.class.getDeclaredFields();
        for (Field field : configurationFields) {
            boolean isAccessible = field.isAccessible();
            try {
                try {
                    field.setAccessible(true);
                    Object value = field.get(configuration);
                    DefaultComponentDescriptor cd = new DefaultComponentDescriptor();
                    cd.setRoleType(value.getClass());
                    componentManager.registerComponent(cd, value);
                } finally {
                    field.setAccessible(isAccessible);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        DefaultComponentDescriptor<MayocatShopConfiguration> cd =
            new DefaultComponentDescriptor<MayocatShopConfiguration>();
        cd.setRoleType(MayocatShopConfiguration.class);
        componentManager.registerComponent(cd, configuration);
    }
}
