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
public class ValidConfigurationEnforcerTest extends AbstractConfigurationTest
{
    private GeneralSettings defaultSettings = new GeneralSettings();

    private GeneralSettings localesNotConfigurablesSettings;

    private final Validator validator = new Validator();

    private final ConfigurationFactory<GeneralSettings> factory =
            ConfigurationFactory.forClass(GeneralSettings.class, validator);

    @Before
    public void setUp() throws Exception
    {
        File notConfigurableConfigurationFile = new File(Resources.getResource(
                "configuration/locales-not-configurable.yml").toURI());
        localesNotConfigurablesSettings = factory.build(notConfigurableConfigurationFile);
    }

    @Test
    public void testEnforceValidConfiguration() throws Exception
    {
        Map<String, Object> generalConfiguration = getConfiguration(defaultSettings);
        Map<String, Object> tenantConfiguration = loadConfiguration("configuration/tenant1.json");
        ValidConfigurationEnforcer enforcer = new ValidConfigurationEnforcer(generalConfiguration, tenantConfiguration);

        ValidConfigurationEnforcer.ValidationResult result = enforcer.enforce();

        Map<String, Object> enforced = result.getResult();
        Assert.assertEquals(false, result.isHasErrors());
        Assert.assertEquals("My shop name", (String) enforced.get("name"));
        Assert.assertEquals(Locale.FRANCE.toString(),
                (String) ((Map<String, Object>) enforced.get("locales")).get("main")
        );
    }

    @Test
    public void testEnforceValidConfigurationWhenNotConfigurable() throws Exception
    {
        Map<String, Object> generalConfiguration = getConfiguration(localesNotConfigurablesSettings);
        Map<String, Object> tenantConfiguration = loadConfiguration("configuration/tenant1.json");
        ValidConfigurationEnforcer enforcer = new ValidConfigurationEnforcer(generalConfiguration, tenantConfiguration);

        ValidConfigurationEnforcer.ValidationResult result = enforcer.enforce();

        Map<String, Object> enforced = result.getResult();
        Assert.assertEquals(true, result.isHasErrors());
        Assert.assertEquals("My shop name", (String) enforced.get("name"));
        Assert.assertTrue(((Map<String, Object>) enforced.get("locales")).isEmpty());
    }
}
