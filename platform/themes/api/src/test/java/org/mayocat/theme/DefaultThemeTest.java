package org.mayocat.theme;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mayocat.configuration.AddonDefinition;
import org.mayocat.configuration.AddonFieldType;
import org.mayocat.configuration.thumbnails.jackson.ThumbnailsModule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.yammer.dropwizard.json.ObjectMapperFactory;

import junit.framework.Assert;

/**
 * @version $Id$
 */
public class DefaultThemeTest
{
    private ObjectMapperFactory objectMapperFactory;

    @Before
    public void setUp() throws Exception
    {
        objectMapperFactory = new ObjectMapperFactory();
        objectMapperFactory.registerModule(new ThumbnailsModule());
    }

    @Test
    public void testParseTheme() throws Exception
    {
        ObjectMapper mapper = objectMapperFactory.build(new YAMLFactory());

        String themeConfig = Resources.toString(Resources.getResource("theme.yml"), Charsets.UTF_8);
        Theme theme = mapper.readValue(themeConfig, DefaultTheme.class);

        Assert.assertEquals("Default theme", theme.getName());
        Assert.assertTrue(theme.getDescription().startsWith("Et harum quidem rerum facilis est et expedita distinctio."));
        Assert.assertEquals(2, theme.getAddons().size());
        AddonDefinition firstAddon = theme.getAddons().get(0);
        Assert.assertEquals("brand", firstAddon.getName());
        Assert.assertEquals(AddonFieldType.STRING, firstAddon.getType());
        List<String> entities = Lists.newArrayList("product");
        Assert.assertEquals(entities, firstAddon.getEntities().get());
        Assert.assertEquals(2, theme.getModels().size());
        Model firstModel = theme.getModels().get(0);
        Assert.assertEquals("Fancy page", firstModel.getName());
        Assert.assertEquals("models/product_fancy.html", firstModel.getFile());
        Assert.assertEquals(entities, firstModel.getEntities().get());

    }

    @Test
    public void testParseEmptyTheme() throws Exception
    {
        ObjectMapper mapper = objectMapperFactory.build(new YAMLFactory());

        String themeConfig = Resources.toString(Resources.getResource("empty-theme.yml"), Charsets.UTF_8);
        Theme theme = mapper.readValue(themeConfig, DefaultTheme.class);

        Assert.assertEquals("Empty theme", theme.getName());
        Assert.assertEquals("", theme.getDescription());
        Assert.assertEquals(0, theme.getAddons().size());
        Assert.assertEquals(0, theme.getModels().size());
    }
}
