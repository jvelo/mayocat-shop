package org.mayocat.shop.application;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mayocat.shop.base.EventListener;
import org.mayocat.shop.base.HealthCheck;
import org.mayocat.shop.base.Managed;
import org.mayocat.shop.base.Provider;
import org.mayocat.shop.base.Task;
import org.mayocat.shop.configuration.AbstractConfiguration;
import org.mayocat.shop.event.ApplicationStartedEvent;
import org.mayocat.shop.rest.resources.Resource;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.observation.ObservationManager;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Environment;

/**
 * @version $Id$
 */
public abstract class AbstractService<C extends AbstractConfiguration> extends Service<C>
{
    private EmbeddableComponentManager componentManager;

    protected abstract void registerComponents(C configuration, Environment environment);

    @Override
    public void run(C configuration, Environment environment) throws Exception
    {
        // Initialize Rendering components and allow getting instances
        componentManager = new EmbeddableComponentManager();

        this.registerConfigurationsAsComponents(configuration);

        this.registerComponents(configuration, environment);

        componentManager.initialize(this.getClass().getClassLoader());

        registerProviders(environment);
        registerResources(environment);
        registerEventListeners(environment);
        registerHealthChecks(environment);
        registerTasks(environment);
        registerManagedServices(environment);

        ObservationManager observationManager = componentManager.getInstance(ObservationManager.class);
        observationManager.notify(new ApplicationStartedEvent(), this);
    }

    protected ComponentManager getComponentManager()
    {
        return this.componentManager;
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
