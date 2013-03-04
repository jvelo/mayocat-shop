package org.mayocat.theme.internal;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import org.mayocat.theme.DefaultTheme;
import org.mayocat.theme.Theme;
import org.mayocat.theme.ThemeLoader;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.io.Resources;
import com.yammer.dropwizard.json.ObjectMapperFactory;

/**
 * @version $Id$
 */
@Component
public class DefaultThemeLoader implements ThemeLoader
{
    @Inject
    private ObjectMapperFactory objectMapperFactory;

    @Inject
    private Logger logger;

    @Override
    public Theme load(String name) throws IOException
    {
        ObjectMapper mapper = objectMapperFactory.build(new YAMLFactory());
        URL url = Resources.getResource("themes/" + name + "/theme.yml");
        if (url == null) {
            logger.error("Theme [{}] could not be found.", name);
            return null;
        }
        final JsonNode node = mapper.readTree(url);
        final Theme theme = mapper.readValue(new TreeTraversingParser(node), DefaultTheme.class);
        return theme;
    }
}
