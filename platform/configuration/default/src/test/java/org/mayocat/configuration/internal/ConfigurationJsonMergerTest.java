package org.mayocat.configuration.internal;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mayocat.configuration.general.GeneralSettings;

import com.google.common.io.Resources;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.validation.Validator;

import junit.framework.Assert;

/**
 * @version $Id$
 */
public class ConfigurationJsonMergerTest extends AbstractConfigurationTest
{
    private GeneralSettings defaultConfiguration = new GeneralSettings();

    private GeneralSettings localesNotConfigurableConfiguration;

    private final Validator validator = new Validator();

    private final ConfigurationFactory<GeneralSettings> factory =
            ConfigurationFactory.forClass(GeneralSettings.class, validator);

    @Before
    public void setUp() throws Exception
    {

        File notConfigurableConfigurationFile = new File(Resources.getResource(
                "configuration/locales-not-configurable.yml").toURI());
        localesNotConfigurableConfiguration = factory.build(notConfigurableConfigurationFile);
    }

    @Test
    public void testConfigurationMerge() throws Exception
    {
        Map<String, Object> generalConfiguration = getConfiguration(defaultConfiguration);
        Map<String, Object> tenantConfiguration = loadConfiguration("configuration/tenant1.json");
        ConfigurationJsonMerger merger = new ConfigurationJsonMerger(generalConfiguration, tenantConfiguration);
        Map<String, Object> merged = merger.merge();
        Assert.assertEquals("My shop name", ((Map<String, Object>) merged.get("name")).get("value"));
        Assert.assertEquals(Locale.FRANCE.toString(),
                ((Map<String, Object>) ((Map<String, Object>) merged.get("locales")).get("main")).get("value")
        );
    }

    @Test
    public void testConfigurationMergeWhenPropertyIsNotConfigurable() throws Exception
    {
        Map<String, Object> generalConfiguration = getConfiguration(localesNotConfigurableConfiguration);
        Map<String, Object> tenantConfiguration = loadConfiguration("configuration/tenant1.json");

        ConfigurationJsonMerger merger = new ConfigurationJsonMerger(generalConfiguration, tenantConfiguration);
        Map<String, Object> merged = merger.merge();
        Assert.assertEquals(Locale.ENGLISH.toString(),
                ((Map<String, Object>) ((Map<String, Object>) merged.get("locales")).get("main")).get("value")
        );
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
