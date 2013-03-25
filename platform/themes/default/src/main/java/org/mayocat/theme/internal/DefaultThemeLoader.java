package org.mayocat.theme.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.inject.Inject;

import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.DefaultTheme;
import org.mayocat.theme.Theme;
import org.mayocat.theme.ThemeLoader;
import org.mayocat.theme.ThemeManager;
import org.mayocat.theme.ThemeResource;
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

    @Inject
    private ThemeManager themeManager;

    @Override
    public Theme load() throws IOException
    {
        ObjectMapper mapper = objectMapperFactory.build(new YAMLFactory());
        ThemeResource themeConfig = themeManager.resolveResource("theme.yml", Breakpoint.DEFAULT);
        JsonNode node;

        if (themeConfig == null) {
            return null; // FIXME => Exception ?
        }

        switch (themeConfig.getType()) {
            default:
            case FILE:
                node = mapper.readTree(new File(themeConfig.getPath()));
                break;
            case CLASSPATH_RESOURCE:
                node = mapper.readTree(Resources.getResource(themeConfig.getPath()));
                break;
        }

        final Theme theme = mapper.readValue(new TreeTraversingParser(node), DefaultTheme.class);
        return theme;
    }
}
