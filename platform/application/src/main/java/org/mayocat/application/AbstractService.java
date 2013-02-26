package org.mayocat.application;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mayocat.base.EventListener;
import org.mayocat.base.HealthCheck;
import org.mayocat.base.Managed;
import org.mayocat.base.Provider;
import org.mayocat.base.Task;
import org.mayocat.configuration.AbstractConfiguration;
import org.mayocat.configuration.thumbnails.jackson.ThumbnailsModule;
import org.mayocat.event.ApplicationStartedEvent;
import org.mayocat.base.Resource;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.observation.ObservationManager;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

/**
 * @version $Id$
 */
public abstract class AbstractService<C extends AbstractConfiguration> extends Service<C>
{
    public static final String ADMIN_UI_PATH = "/admin/";

    private EmbeddableComponentManager componentManager;

    protected abstract void registerComponents(C configuration, Environment environment);

    public static final Set<String> STATIC_PATHS = new HashSet<String>(){{
        add(ADMIN_UI_PATH);
    }};


    @Override
    public void initialize(Bootstrap<C> bootstrap)
    {
        bootstrap.getObjectMapperFactory().registerModule(new ThumbnailsModule());
    }

    @Override
    public void run(C configuration, Environment environment) throws Exception
    {
        this.initializeComponentManager(configuration, environment);

        registerProviders(environment);
        registerResources(environment);
        registerEventListeners(environment);
        registerHealthChecks(environment);
        registerTasks(environment);
        registerManagedServices(environment);

        ObservationManager observationManager = getComponentManager().getInstance(ObservationManager.class);
        observationManager.notify(new ApplicationStartedEvent(), this);
    }

    protected ComponentManager getComponentManager()
    {
        return this.componentManager;
    }

    protected void initializeComponentManager(C configuration, Environment environment)
    {
        componentManager = new EmbeddableComponentManager();
        this.registerConfigurationsAsComponents(configuration);
        this.registerComponents(configuration, environment);
        componentManager.initialize(this.getClass().getClassLoader());
    }

    private void registerManagedServices(Environment environment) throws ComponentLookupException
    {
        // Managed services that show a managed lifecycle
        Map<String, Managed> managedServices = componentManager.getInstanceMap(Managed.class);
        for (Map.Entry<String, Managed> managed : managedServices.entrySet()) {
            environment.manage(managed.getValue());
        }
    }

    private void registerTasks(Environment environment) throws ComponentLookupException
    {
        // Registering tasks implementations against the environment
        Map<String, Task> tasks = componentManager.getInstanceMap(Task.class);
        for (Map.Entry<String, Task> task : tasks.entrySet()) {
            if (com.yammer.dropwizard.tasks.Task.class.isAssignableFrom(task.getValue().getClass())) {
                environment.addTask((com.yammer.dropwizard.tasks.Task) task.getValue());
            }
        }
    }

    private void registerHealthChecks(Environment environment) throws ComponentLookupException
    {
        // Registering health checks implementations against the environment
        Map<String, HealthCheck> healthChecks = componentManager.getInstanceMap(HealthCheck.class);
        for (Map.Entry<String, HealthCheck> check : healthChecks.entrySet()) {
            if (com.yammer.metrics.core.HealthCheck.class.isAssignableFrom(check.getValue().getClass())) {
                environment.addHealthCheck((com.yammer.metrics.core.HealthCheck) check.getValue());
            }
        }
    }

    private void registerEventListeners(Environment environment) throws ComponentLookupException
    {
        // Registering revent listeners implementations against the environment
        Map<String, EventListener> eventListeners = componentManager.getInstanceMap(EventListener.class);
        for (Map.Entry<String, EventListener> listener : eventListeners.entrySet()) {
            environment.addServletListeners(listener.getValue());
        }
    }

    private void registerResources(Environment environment) throws ComponentLookupException
    {
        // Registering resources component implementations against the environment...
        Map<String, Resource> restResources = componentManager.getInstanceMap(Resource.class);
        for (Map.Entry<String, Resource> resource : restResources.entrySet()) {
            environment.addResource(resource.getValue());
        }
    }

    private void registerProviders(Environment environment) throws ComponentLookupException
    {
        // Registering provider component implementations against the environment...
        Map<String, Resource> providers = componentManager.getInstanceMap(Provider.class);
        for (Map.Entry<String, Resource> provider : providers.entrySet()) {
            environment.addProvider(provider.getValue());
        }
    }

    private void registerConfigurationsAsComponents(C configuration)
    {
        List<Field> configurationFields = getAllFields(configuration.getClass());
        for (Field field : configurationFields) {
            boolean isAccessible = field.isAccessible();
            try {
                try {
                    field.setAccessible(true);
                    Object value = field.get(configuration);

                    // Inject "as is" for components that only need an individual configuration
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

        DefaultComponentDescriptor<C> cd =
                new DefaultComponentDescriptor<C>();
        cd.setRoleType(configuration.getClass());
        componentManager.registerComponent(cd, configuration);
    }

    private static List<Field> getAllFields(Class<?> type)
    {
        List<Field> fields = new ArrayList<Field>();

        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields.addAll(getAllFields(type.getSuperclass()));
        }

        return fields;
    }
}
