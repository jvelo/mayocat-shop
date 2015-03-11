/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.application;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.mayocat.Module;
import org.mayocat.accounts.AccountsModule;
import org.mayocat.configuration.AbstractSettings;
import org.mayocat.configuration.ExposedSettings;
import org.mayocat.configuration.jackson.NIOModule;
import org.mayocat.configuration.jackson.TimeZoneModule;
import org.mayocat.context.FlashScopeCookieContainerFilter;
import org.mayocat.context.SessionScopeCookieContainerFilter;
import org.mayocat.event.ApplicationStartedEvent;
import org.mayocat.event.EventListener;
import org.mayocat.health.HealthCheck;
import org.mayocat.internal.meta.DefaultEntityMetaRegistry;
import org.mayocat.jackson.PermissiveFuzzyEnumModule;
import org.mayocat.jersey.MayocatFullContextRequestFilter;
import org.mayocat.lifecycle.Managed;
import org.mayocat.localization.LocalizationContainerFilter;
import org.mayocat.meta.EntityMeta;
import org.mayocat.meta.EntityMetaRegistry;
import org.mayocat.multitenancy.MultitenancyContainerFilter;
import org.mayocat.rest.Provider;
import org.mayocat.rest.Resource;
import org.mayocat.rest.jackson.MayocatGroovyModule;
import org.mayocat.rest.jackson.MayocatJodaModule;
import org.mayocat.rest.jackson.MayocatLocaleBCP47LanguageTagModule;
import org.mayocat.rest.jersey.CorsResponseFilter;
import org.mayocat.servlet.ServletFilter;
import org.mayocat.task.Task;
import org.mayocat.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.observation.ObservationManager;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.core.ResourceConfig;

import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jackson.GuavaExtrasModule;
import io.dropwizard.jackson.LogbackModule;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @version $Id$
 */
public abstract class AbstractService<C extends AbstractSettings> extends Application<C>
{
    protected static Set<String> staticPaths = new HashSet<>();

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AbstractService.class);

    protected abstract void registerComponents(C configuration, Environment environment);

    private EmbeddableComponentManager componentManager;

    private Map<String, Module> modules = Maps.newHashMap();

    private List<Class> requestFilters = Lists.newArrayList();

    private List<Class> responseFilters = Lists.newArrayList();

    private ObjectMapper objectMapper;

    public static Set<String> getStaticPaths()
    {
        return staticPaths;
    }

    @Override
    public void initialize(Bootstrap<C> bootstrap)
    {
        this.addModule(new AccountsModule());

        bootstrap.getObjectMapper().registerModule(new NIOModule());
        bootstrap.getObjectMapper().registerModule(new MayocatJodaModule());
        bootstrap.getObjectMapper().registerModule(new MayocatLocaleBCP47LanguageTagModule());
        bootstrap.getObjectMapper().registerModule(new TimeZoneModule());

        bootstrap.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // TODO: remove when upgrading DW to 0.8
        bootstrap.getObjectMapper().registerModule(new PermissiveFuzzyEnumModule());
    }

    @Override
    public void run(C configuration, Environment environment) throws Exception
    {
        configureObjectMapper();

        this.initializeComponentManager(configuration, environment);
        registerServletFilters(environment);
        registerProviders(environment);
        registerResources(environment);
        registerEventListeners(environment);
        registerHealthChecks(environment);
        registerTasks(environment);
        registerManagedServices(environment);

        // Default Jersey filters
        addRequestFilter(SessionScopeCookieContainerFilter.class);
        addRequestFilter(FlashScopeCookieContainerFilter.class);
        addRequestFilter(LocalizationContainerFilter.class);
        addRequestFilter(MultitenancyContainerFilter.class);
        addResponseFilter(SessionScopeCookieContainerFilter.class);
        addResponseFilter(FlashScopeCookieContainerFilter.class);
        addResponseFilter(CorsResponseFilter.class);
        addResponseFilter(MayocatFullContextRequestFilter.class);

        // Register Jersey container response filters
        Map<String, Object> jerseyPropertiesAndFeatures = Maps.newHashMap();
        jerseyPropertiesAndFeatures.put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS,
                Joiner.on(",").join(
                        Collections2.transform(this.requestFilters, new Function<Class, String>()
                        {
                            public String apply(Class string)
                            {
                                return string.getCanonicalName();
                            }
                        })
                ));
        jerseyPropertiesAndFeatures.put(
                ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS,
                Joiner.on(",").join(
                        Collections2.transform(this.responseFilters, new Function<Class, String>()
                        {
                            public String apply(Class string)
                            {
                                return string.getCanonicalName();
                            }
                        })
                )
        );

        // Register Jersey container request filters
        environment.jersey().getResourceConfig().setPropertiesAndFeatures(jerseyPropertiesAndFeatures);

        ObservationManager observationManager = getComponentManager().getInstance(ObservationManager.class);
        observationManager.notify(new ApplicationStartedEvent(), this);
    }

    public final void addRequestFilter(Class clazz)
    {
        this.requestFilters.add(clazz);
    }

    public final void addResponseFilter(Class clazz)
    {
        this.responseFilters.add(clazz);
    }

    protected void configureObjectMapper()
    {
        // Initialize our own object mapper. We don't want to use Dropwizard's one (environment.getObjectMapper) because
        // we don't have full control over its initialization, and we don't necessarily want mayocat's one to be
        // configured identically as the one used by DW.

        objectMapper = new ObjectMapper(new YAMLFactory());
        // Standard modules
        objectMapper.registerModule(new GuavaModule());
        objectMapper.registerModule(new JodaModule());
        objectMapper.registerModule(new AfterburnerModule());
        // Dropwizard modules
        objectMapper.registerModule(new GuavaExtrasModule());
        objectMapper.registerModule(new LogbackModule());
        // Mayocat modules
        objectMapper.registerModule(new TimeZoneModule());
        objectMapper.registerModule(new NIOModule());
        objectMapper.registerModule(new MayocatJodaModule());
        objectMapper.registerModule(new MayocatLocaleBCP47LanguageTagModule());
        objectMapper.registerModule(new MayocatGroovyModule());

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected void addModule(Module module)
    {
        if (this.modules.containsKey(module.getName())) {
            LOGGER.error("Module with name [" + module.getName() + "] already exists. Refusing to start.");
            System.exit(1);
        }
        this.modules.put(module.getName(), module);
    }

    protected ComponentManager getComponentManager()
    {
        return this.componentManager;
    }

    protected void initializeComponentManager(C configuration, Environment environment)
    {
        componentManager = new EmbeddableComponentManager();

        this.registerSettingsAsComponents(configuration);
        this.registerObjectMapper(objectMapper);
        this.registerJerseyClient(configuration, environment);
        this.registerEntityMetaRegistryAsComponent();
        this.registerComponents(configuration, environment);

        componentManager.initialize(this.getClass().getClassLoader());

        Utils.setComponentManager(componentManager);
    }

    private void registerServletFilters(Environment environment) throws ComponentLookupException
    {
        Map<String, ServletFilter> servletFilters = componentManager.getInstanceMap(ServletFilter.class);
        for (Map.Entry<String, ServletFilter> filter : servletFilters.entrySet()) {
            if (!Filter.class.isAssignableFrom(filter.getValue().getClass())) {
                LOGGER.warn("Ignoring servlet filter of class {} which does not implement Filter");
            } else {
                environment.servlets()
                        .addFilter(filter.getValue().getClass().getSimpleName(), (Filter) filter.getValue())
                        .addMappingForUrlPatterns(
                                EnumSet.of(DispatcherType.REQUEST), true, filter.getValue().urlPattern());
            }
        }
    }

    private void registerManagedServices(Environment environment) throws ComponentLookupException
    {
        // Managed services that show a managed lifecycle
        Map<String, Managed> managedServices = componentManager.getInstanceMap(Managed.class);
        for (Map.Entry<String, Managed> managed : managedServices.entrySet()) {
            environment.lifecycle().manage(managed.getValue());
        }
    }

    private void registerTasks(Environment environment) throws ComponentLookupException
    {
        // Registering tasks implementations against the environment
        Map<String, Task> tasks = componentManager.getInstanceMap(Task.class);
        for (Map.Entry<String, Task> task : tasks.entrySet()) {
            if (io.dropwizard.servlets.tasks.Task.class.isAssignableFrom(task.getValue().getClass())) {
                environment.admin().addTask((io.dropwizard.servlets.tasks.Task) task.getValue());
            }
        }
    }

    private void registerHealthChecks(Environment environment) throws ComponentLookupException
    {
        // Registering health checks implementations against the environment
        Map<String, HealthCheck> healthChecks = componentManager.getInstanceMap(HealthCheck.class);
        for (Map.Entry<String, HealthCheck> check : healthChecks.entrySet()) {
            if (com.codahale.metrics.health.HealthCheck.class.isAssignableFrom(check.getValue().getClass())) {
                environment.healthChecks()
                        .register(check.getKey(), (com.codahale.metrics.health.HealthCheck) check.getValue());
            }
        }
    }

    private void registerEventListeners(Environment environment) throws ComponentLookupException
    {
        // Registering revent listeners implementations against the environment
        Map<String, EventListener> eventListeners = componentManager.getInstanceMap(EventListener.class);
        for (Map.Entry<String, EventListener> listener : eventListeners.entrySet()) {
            environment.servlets().addServletListeners(listener.getValue());
        }
    }

    private void registerResources(Environment environment) throws ComponentLookupException
    {
        // Registering resources component implementations against the environment...
        Map<String, Resource> restResources = componentManager.getInstanceMap(Resource.class);
        for (Map.Entry<String, Resource> resource : restResources.entrySet()) {
            environment.jersey().register(resource.getValue());
        }
    }

    private void registerProviders(Environment environment) throws ComponentLookupException
    {
        // Registering provider component implementations against the environment...
        Map<String, Resource> providers = componentManager.getInstanceMap(Provider.class);
        for (Map.Entry<String, Resource> provider : providers.entrySet()) {
            environment.jersey().register(provider.getValue());
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

    private void registerJerseyClient(AbstractSettings settings, Environment environment)
    {
        final Client client = new JerseyClientBuilder(environment).using(settings.getJerseyClientConfiguration())
                .build(getName());
        DefaultComponentDescriptor<Client> cd = new DefaultComponentDescriptor<Client>();
        cd.setRoleType(Client.class);
        componentManager.registerComponent(cd, client);
    }


    private void registerObjectMapper(ObjectMapper mapper)
    {
        DefaultComponentDescriptor<ObjectMapper> cd = new DefaultComponentDescriptor<ObjectMapper>();
        cd.setRoleType(ObjectMapper.class);
        componentManager.registerComponent(cd, mapper);
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
