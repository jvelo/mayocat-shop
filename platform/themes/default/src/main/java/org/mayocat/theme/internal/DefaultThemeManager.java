package org.mayocat.theme.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Optional;
import com.google.common.io.Resources;
import com.yammer.dropwizard.json.ObjectMapperFactory;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.FilesSettings;
import org.mayocat.configuration.theme.ThemeSettings;
import org.mayocat.context.Execution;
import org.mayocat.theme.*;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import javax.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @version $Id$
 */
@Component
public class DefaultThemeManager implements ThemeManager
{
    private final static String THEMES_FOLDER_NAME = "themes";

    private final static String TENANTS_FOLDER_NAME = "tenants";

    @Inject
    private ObjectMapperFactory objectMapperFactory;

    @Inject
    private Logger logger;

    @Inject
    private ThemeFileResolver themeFileResolver;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private FilesSettings filesSettings;

    @Inject
    private Execution execution;

    @Override
    public Theme getTheme()
    {
        return getTheme(this.execution.getContext().getTenant());
    }

    @Override
    public Theme getTheme(Tenant tenant)
    {
        try {
            ObjectMapper mapper = objectMapperFactory.build(new YAMLFactory());
            JsonNode node;

            String themeId = getActiveThemeId(tenant);

            boolean isThemeInTenantDirectory = true;
            File themeDirectory = getTenantThemeDirectory(tenant.getSlug(), themeId);
            if (!themeDirectory.exists()) {
                isThemeInTenantDirectory = false;
                themeDirectory = getGlobalThemeDirectory(themeId);

                if (!themeDirectory.exists()) {
                    Optional<String> path = getClasspathThemeYmlPath(themeId);
                    if (path.isPresent()) {
                        node = mapper.readTree(Resources.getResource(path.get()));
                        ThemeDefinition definition =
                                mapper.readValue(new TreeTraversingParser(node), ThemeDefinition.class);

                        Theme theme = new Theme(Paths.get(path.get()), definition, null, Theme.Type.FILE_SYSTEM);
                        return theme;
                    } else {
                        return null;
                    }
                }
            }

            node = mapper.readTree(new File(themeDirectory.getPath() + "/theme.yml"));
            ThemeDefinition definition = mapper.readValue(new TreeTraversingParser(node), ThemeDefinition.class);

            Theme theme = new Theme(themeDirectory.toPath(), definition, null, Theme.Type.FILE_SYSTEM);
            if (isThemeInTenantDirectory) {
                // The theme lives in the tenant directory
                theme.setTenantOwnTheme(true);
            }

            return theme;
        } catch (IOException e) {
            logger.warn("Failed to load a theme from configuration ; using default theme");
            return null;
        }
    }

    private File getTenantThemeDirectory(String tenantSlug, String themeId)
    {
        String tenantThemePath = filesSettings.getPermanentDirectory() + "/" + TENANTS_FOLDER_NAME + "/" + tenantSlug
                + "/" + THEMES_FOLDER_NAME + "/" + themeId + "/";

        File file = new File(tenantThemePath);
        return file;
    }

    private File getGlobalThemeDirectory(String themeId)
    {
        String themePath =
                filesSettings.getPermanentDirectory() + "/" + THEMES_FOLDER_NAME + "/" + themeId + "/";

        File file = new File(themePath);
        return file;
    }

    private Optional<String> getClasspathThemeYmlPath(String themeId)
    {
        String themePath = THEMES_FOLDER_NAME + "/" + themeId + "/";
        try {
            Resources.getResource(themePath);
            return Optional.of(themePath);
        } catch (IllegalArgumentException e) {
            return Optional.absent();
        }
    }

    private String getActiveThemeId(Tenant tenant)
    {
        ThemeSettings settings = configurationService.getSettings(ThemeSettings.class, tenant);
        return settings.getActive().getValue();
    }
}
