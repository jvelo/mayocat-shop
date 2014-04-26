/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.internal;

import java.io.File;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.configuration.general.GeneralSettings;

import com.google.common.io.Resources;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.validation.Validator;

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
        Map<String, Serializable> generalConfiguration = getConfiguration(defaultSettings);
        Map<String, Serializable> tenantConfiguration = loadConfiguration("configuration/tenant1.json");
        ValidConfigurationEnforcer enforcer = new ValidConfigurationEnforcer(generalConfiguration, tenantConfiguration);

        ValidConfigurationEnforcer.ValidationResult result = enforcer.enforce();

        Map<String, Serializable> enforced = result.getResult();
        Assert.assertEquals(false, result.isHasErrors());
        Assert.assertEquals(Locale.FRANCE.toString(),
                (String) ((Map<String, Serializable>) enforced.get("locales")).get("main")
        );
    }

    @Test
    public void testEnforceValidConfigurationWhenNotConfigurable() throws Exception
    {
        Map<String, Serializable> generalConfiguration = getConfiguration(localesNotConfigurablesSettings);
        Map<String, Serializable> tenantConfiguration = loadConfiguration("configuration/tenant1.json");
        ValidConfigurationEnforcer enforcer = new ValidConfigurationEnforcer(generalConfiguration, tenantConfiguration);

        ValidConfigurationEnforcer.ValidationResult result = enforcer.enforce();

        Map<String, Serializable> enforced = result.getResult();
        Assert.assertEquals(true, result.isHasErrors());
        Assert.assertTrue(((Map<String, Serializable>) enforced.get("locales")).isEmpty());
    }
}
