package org.mayocat.application;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mayocat.context.CookieSessionContainerFilter;
import org.mayocat.event.EventListener;
import org.mayocat.configuration.ExposedSettings;
import org.mayocat.health.HealthCheck;
import org.mayocat.internal.meta.DefaultEntityMetaRegistry;
import org.mayocat.lifecycle.Managed;
import org.mayocat.Module;
import org.mayocat.meta.EntityMeta;
import org.mayocat.meta.EntityMetaRegistry;
import org.mayocat.rest.Provider;
import org.mayocat.task.Task;
import org.mayocat.accounts.AccountsModule;
import org.mayocat.configuration.AbstractSettings;
import org.mayocat.configuration.jackson.TimeZoneModule;
import org.mayocat.event.ApplicationStartedEvent;
import org.mayocat.rest.Resource;
import org.mayocat.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.observation.ObservationManager;

import com.google.common.collect.Maps;
import com.sun.jersey.api.core.ResourceConfig;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.json.ObjectMapperFactory;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * @version $Id$
 */
public abstract class AbstractService<C extends AbstractSettings> extends Service<C>
{
    public static final String ADMIN_UI_PATH = "/admin/";

    private EmbeddableComponentManager componentManager;

    private ObjectMapperFactory objectMapperFactory;

    private Map<String, Module> modules = Maps.newHashMap();

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AbstractService.class);

    protected abstract void registerComponents(C configuration, Environment environment);

    public static final Set<String> STATIC_PATHS = new HashSet<String>()
    {
        {
            add(ADMIN_UI_PATH);
        }
    };

    protected void addModule(Module module)
    {
        if (this.modules.containsKey(module.getName())) {
            LOGGER.error("Module with name [" + module.getName() + "] already exists. Refusing to start.");
            System.exit(1);
        }
        this.modules.put(module.getName(), module);
    }

    @Override
    public void initialize(Bootstrap<C> bootstrap)
    {
        this.objectMapperFactory = bootstrap.getObjectMapperFactory();

        this.objectMapperFactory.registerModule(new TimeZoneModule());
        this.objectMapperFactory.registerModule(new JodaModule());


        this.addModule(new AccountsModule());
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

        environment.setJerseyProperty(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS,
                CookieSessionContainerFilter.class.getCanonicalName());
        environment.setJerseyProperty(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS,
                CookieSessionContainerFilter.class.getCanonicalName());

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

        this.registerSettingsAsComponents(configuration);
        this.registerObjectMapperFactoryAsComponent();
        this.registerEntityMetaRegistryAsComponent();
        this.registerComponents(configuration, environment);

        componentManager.initialize(this.getClass().getClassLoader());

        Utils.setComponentManager(componentManager);
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

    private void registerEntityMetaRegistryAsComponent()
    {
        Map<String, EntityMeta> entities = Maps.newHashMap();

        for (Module module : modules.values()) {
            for (EntityMeta meta : module.getEntities()) {
                if (entities.containsKey(meta.getEntityName())) {
                    LOGGER.error(
                            "Entity with name [" + meta.getEntityName() + "] already registered. Refusing to start.");
                    System.exit(1);
                }
                entities.put(meta.getEntityName(), meta);
            }
        }

        EntityMetaRegistry registry = new DefaultEntityMetaRegistry(new ArrayList(entities.values()));

        DefaultComponentDescriptor<EntityMetaRegistry> cd = new DefaultComponentDescriptor<EntityMetaRegistry>();
        cd.setRoleType(EntityMetaRegistry.class);

        componentManager.registerComponent(cd, registry);
    }

    private void registerObjectMapperFactoryAsComponent()
    {
        DefaultComponentDescriptor<ObjectMapperFactory> cd = new DefaultComponentDescriptor<ObjectMapperFactory>();
        cd.setRoleType(ObjectMapperFactory.class);
        componentManager.registerComponent(cd, this.objectMapperFactory);
    }

    private void registerSettingsAsComponents(C settings)
    {
        List<Field> settingsFields = getAllFields(settings.getClass());
        for (Field field : settingsFields) {
            boolean isAccessible = field.isAccessible();
            try {
                try {
                    field.setAccessible(true);
                    Object value = field.get(settings);

                    // Inject "as is" for components that only need an individual settings
                    DefaultComponentDescriptor cd = new DefaultComponentDescriptor();
                    cd.setRoleType(value.getClass());
                    componentManager.registerComponent(cd, value);

                    if (ExposedSettings.class.isAssignableFrom(value.getClass())) {

                        // Inject as settings
                        ExposedSettings exposedSettings = (ExposedSettings) value;
                        DefaultComponentDescriptor cd2 = new DefaultComponentDescriptor();
                        cd2.setRoleType(ExposedSettings.class);
                        cd2.setRoleHint(exposedSettings.getKey());
                        componentManager.registerComponent(cd2, value);
                    }
                } finally {
                    field.setAccessible(isAccessible);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        DefaultComponentDescriptor<C> cd =
                new DefaultComponentDescriptor<C>();
        cd.setRoleType(settings.getClass());
        componentManager.registerComponent(cd, settings);
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
