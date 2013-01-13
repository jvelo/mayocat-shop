package org.mayocat.shop.service.internal;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mayocat.shop.configuration.general.GeneralConfiguration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.validation.Validator;

import junit.framework.Assert;

/**
 * @version $Id$
 */
public class ConfigurationMergerTest
{
    private GeneralConfiguration defaultConfiguration = new GeneralConfiguration();

    private GeneralConfiguration notConfigurableConfiguration;

    private final Validator validator = new Validator();

    private final ConfigurationFactory<GeneralConfiguration> factory =
            ConfigurationFactory.forClass(GeneralConfiguration.class, validator);

    @Before
    public void setUp() throws Exception
    {
        File notConfigurableConfigurationFile = new File(Resources.getResource("configuration/not-configurable.yml").toURI());
        notConfigurableConfiguration = factory.build(notConfigurableConfigurationFile);
    }

    @Test
    public void testConfigurationMerge() throws Exception
    {
        Map<String, Object> generalConfiguration = getConfiguration(defaultConfiguration);
        Map<String, Object> tenantConfiguration = loadConfiguration("configuration/tenant1.json");
        ConfigurationMerger merger = new ConfigurationMerger(generalConfiguration, tenantConfiguration);
        Map<String, Object> merged = merger.merge();
        Assert.assertEquals("My shop name", ((Map<String, Object>) merged.get("name")).get("value"));
        Assert.assertEquals(Locale.FRANCE.toString(),
                ((Map<String, Object>) ((Map<String, Object>) merged.get("locales")).get("main")).get("value")
        );
    }

    @Test
    public void testConfigurationMergeWhenPropertyIsNotConfigurable() throws Exception
    {
        Map<String, Object> generalConfiguration = getConfiguration(notConfigurableConfiguration);
        Map<String, Object> tenantConfiguration = loadConfiguration("configuration/tenant1.json");

        ConfigurationMerger merger = new ConfigurationMerger(generalConfiguration, tenantConfiguration);
        Map<String, Object> merged = merger.merge();
        Assert.assertEquals(Locale.ENGLISH.toString(),
                ((Map<String, Object>) ((Map<String, Object>) merged.get("locales")).get("main")).get("value")
        );
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Map<String, Object> loadConfiguration(String uri) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(loadJSON(uri), new TypeReference<Map<String, Object>>()
        {
        });
    }

    private String loadJSON(String uri) throws IOException
    {
        return Resources.toString(Resources.getResource(uri), Charsets.UTF_8);
    }

    private Map<String, Object> getConfiguration(GeneralConfiguration configuration) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        String asJson = mapper.writeValueAsString(configuration);
        Map<String, Object> configurationAsJson = mapper.readValue(asJson, new TypeReference<Map<String, Object>>(){});
        return configurationAsJson;
    }
}