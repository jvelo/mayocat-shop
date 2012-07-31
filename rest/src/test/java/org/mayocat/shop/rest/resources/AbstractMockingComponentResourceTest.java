package org.mayocat.shop.rest.resources;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.Module;
import org.junit.After;
import org.junit.Before;
import org.xwiki.test.AbstractMockingComponentTestCase;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.yammer.dropwizard.bundles.JavaBundle;
import com.yammer.dropwizard.jersey.DropwizardResourceConfig;
import com.yammer.dropwizard.jersey.JacksonMessageBodyProvider;
import com.yammer.dropwizard.json.Json;

/**
 * This class is copied over as is from DropWizard, except it extends {@link AbstractMockingComponentTestCase}
 * So that tests that extends this class can benefit from component dependencies mocks setups.
 */
public abstract class AbstractMockingComponentResourceTest extends AbstractMockingComponentTestCase
{
    private final Set<Object> singletons = Sets.newHashSet();
    private final Set<Class<?>> providers = Sets.newHashSet();
    private final List<Module> modules = Lists.newArrayList();
    private final Map<String, Boolean> features = Maps.newHashMap();

    private JerseyTest test;

    protected abstract void setUpResources() throws Exception;

    protected void addResource(Object resource) {
        singletons.add(resource);
    }

    public void addProvider(Class<?> klass) {
        providers.add(klass);
    }

    protected void addJacksonModule(Module module) {
        modules.add(module);
    }

    protected void addFeature(String feature, Boolean value) {
        features.put(feature, value);
    }

    protected Json getJson() {
        final Json json = new Json();
        for (Module module : modules) {
            json.registerModule(module);
        }
        return json;
    }
    
    protected Client client() {
        return test.client();
    }

    @Before
    public void setUpJersey() throws Exception {
        setUpResources();
        this.test = new JerseyTest() {
            @Override
            protected AppDescriptor configure() {
                final DropwizardResourceConfig config = new DropwizardResourceConfig(true);
                for (Object provider : JavaBundle.DEFAULT_PROVIDERS) { // sorry, Scala folks
                    config.getSingletons().add(provider);
                }
                for (Class<?> provider : providers) {
                    config.getClasses().add(provider);
                }
                final Json json = getJson();
                for (Map.Entry<String, Boolean> feature : features.entrySet()) {
                    config.getFeatures().put(feature.getKey(), feature.getValue());
                }
                config.getSingletons().add(new JacksonMessageBodyProvider(json));
                config.getSingletons().addAll(singletons);
                return new LowLevelAppDescriptor.Builder(config).build();
            }
        };
        test.setUp();
    }

    @After
    public void tearDownJersey() throws Exception {
        if (test != null) {
            test.tearDown();
        }
    }
}
