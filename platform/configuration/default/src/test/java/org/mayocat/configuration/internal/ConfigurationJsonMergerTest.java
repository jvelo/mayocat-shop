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

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.configuration.general.GeneralSettings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import io.dropwizard.configuration.ConfigurationFactory;

/**
 * @version $Id$
 */
public class ConfigurationJsonMergerTest extends AbstractConfigurationTest
{
    private GeneralSettings defaultConfiguration = new GeneralSettings();

    private GeneralSettings localesNotConfigurableConfiguration;

    private Validator validator;

    private ObjectMapper objectMapper = new ObjectMapper();

    private ConfigurationFactory<GeneralSettings> factory;

    @Before
    public void setUp() throws Exception
    {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();

        factory = new ConfigurationFactory<>(GeneralSettings.class, validator, objectMapper, "foo");

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
