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
        Map<String, Serializable> generalConfiguration = getConfiguration(defaultConfiguration);
        Map<String, Serializable> tenantConfiguration = loadConfiguration("configuration/tenant1.json");
        ConfigurationJsonMerger merger = new ConfigurationJsonMerger(generalConfiguration, tenantConfiguration);
        Map<String, Serializable> merged = merger.merge();
        Assert.assertEquals(Locale.FRANCE.toString(),
                ((Map<String, Object>) ((Map<String, Object>) merged.get("locales")).get("main")).get("value")
        );
    }

    @Test
    public void testConfigurationMergeWhenPropertyIsNotConfigurable() throws Exception
    {
        Map<String, Serializable> generalConfiguration = getConfiguration(localesNotConfigurableConfiguration);
        Map<String, Serializable> tenantConfiguration = loadConfiguration("configuration/tenant1.json");

        ConfigurationJsonMerger merger = new ConfigurationJsonMerger(generalConfiguration, tenantConfiguration);
        Map<String, Serializable> merged = merger.merge();
        Assert.assertEquals(Locale.ENGLISH.toString(),
                ((Map<String, Object>) ((Map<String, Object>) merged.get("locales")).get("main")).get("value")
        );
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
