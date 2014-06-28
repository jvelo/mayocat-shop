/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.theme;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mayocat.addons.model.AddonFieldDefinition;
import org.mayocat.addons.model.AddonGroupDefinition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.yammer.dropwizard.json.ObjectMapperFactory;

import junit.framework.Assert;

/**
 * @version $Id$
 */
public class ThemeDefinitionTest
{
    private ObjectMapperFactory objectMapperFactory;

    @Before
    public void setUp() throws Exception
    {
        objectMapperFactory = new ObjectMapperFactory();
    }

    @Test
    public void testParseTheme() throws Exception
    {
        ObjectMapper mapper = objectMapperFactory.build(new YAMLFactory());

        String themeConfig = Resources.toString(Resources.getResource("theme.yml"), Charsets.UTF_8);
        ThemeDefinition theme = mapper.readValue(themeConfig, ThemeDefinition.class);

        Assert.assertEquals("Default theme", theme.getName());
        Assert.assertTrue(
                theme.getDescription().startsWith("Et harum quidem rerum facilis est et expedita distinctio."));
        Assert.assertEquals(2, theme.getAddons().size());

        Map<String, AddonGroupDefinition> addons = theme.getAddons();
        Assert.assertEquals(2, addons.keySet().size());

        AddonGroupDefinition group1 = addons.get("group1");
        Assert.assertEquals("Addon group 1", group1.getName());
        Assert.assertEquals("Short help text that is displayed under the addon group", group1.getText());
        List<String> entities = Lists.newArrayList("product", "page");
        Assert.assertEquals(Optional.of(entities), group1.getEntities());
        Map<String, AddonFieldDefinition> fields = group1.getFields();
        AddonFieldDefinition brand = fields.get("brand");
        Assert.assertEquals("Brand", brand.getName());
        Assert.assertEquals("string", brand.getType());

        AddonGroupDefinition group2 = addons.get("group2");
        Map<String, AddonFieldDefinition> fields2 = group2.getFields();
        Assert.assertEquals(2, fields2.keySet().size());
        AddonFieldDefinition field1 = fields2.get("field1");
        Assert.assertEquals("Field 1", field1.getName());
    }

    @Test
    public void testParsePagination() throws Exception
    {
        ObjectMapper mapper = objectMapperFactory.build(new YAMLFactory());

        String themeConfig = Resources.toString(Resources.getResource("pagination-theme.yml"), Charsets.UTF_8);
        ThemeDefinition theme = mapper.readValue(themeConfig, ThemeDefinition.class);

        Map<String, PaginationDefinition> paginationDefinitions = theme.getPaginationDefinitions();

        Assert.assertNotNull(paginationDefinitions.get("products"));
        Assert.assertEquals(new Integer(10), paginationDefinitions.get("products").getItemsPerPage());

        PaginationDefinition collection = paginationDefinitions.get("collection");
        Assert.assertNotNull(collection);
        Assert.assertEquals(new Integer(25), collection.getItemsPerPage());
        Assert.assertNotNull(collection.getModels().get("custom-model"));
        Assert.assertEquals(new Integer(12), collection.getModels().get("custom-model").getItemsPerPage());
    }

    @Test
    public void testParseTypes() throws Exception
    {
        ObjectMapper mapper = objectMapperFactory.build(new YAMLFactory());

        String themeConfig = Resources.toString(Resources.getResource("theme-with-types.yml"), Charsets.UTF_8);
        ThemeDefinition theme = mapper.readValue(themeConfig, ThemeDefinition.class);

        Assert.assertTrue(theme.getProductTypes().size() > 0);
        TypeDefinition typeShirt = theme.getProductTypes().get("shirt");

        Assert.assertNotNull(typeShirt);
        Assert.assertEquals("T-Shirt", typeShirt.getName());

        FeatureDefinition colorFeature = typeShirt.getFeatures().get("color");
        Assert.assertNotNull(colorFeature);
        Assert.assertEquals("Color", colorFeature.getName());
        Assert.assertEquals(0, colorFeature.getKeys().size());
        Assert.assertEquals(0, colorFeature.getAddons().size());

        FeatureDefinition sizeFeature = typeShirt.getFeatures().get("size");
        Assert.assertNotNull(sizeFeature);
        Assert.assertEquals("Size", sizeFeature.getName());
        Assert.assertEquals(3, sizeFeature.getKeys().size());
        Assert.assertEquals(1, sizeFeature.getAddons().size());

        VariantsDefinition variants = typeShirt.getVariants();
        Assert.assertEquals(3, variants.getProperties().size());
        Assert.assertEquals(1, variants.getAddons().size());
    }

    @Test
    public void testParseEmptyTheme() throws Exception
    {
        ObjectMapper mapper = objectMapperFactory.build(new YAMLFactory());

        String themeConfig = Resources.toString(Resources.getResource("empty-theme.yml"), Charsets.UTF_8);
        ThemeDefinition theme = mapper.readValue(themeConfig, ThemeDefinition.class);

        Assert.assertEquals("Empty theme", theme.getName());
        Assert.assertEquals("", theme.getDescription());
        Assert.assertEquals(0, theme.getAddons().size());
        Assert.assertEquals(0, theme.getModels().size());
        Assert.assertEquals(0, theme.getProductTypes().size());
    }
}

