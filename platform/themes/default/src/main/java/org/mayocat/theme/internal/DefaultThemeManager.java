package org.mayocat.theme.internal;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.FilesSettings;
import org.mayocat.configuration.theme.ThemeSettings;
import org.mayocat.context.Execution;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.Model;
import org.mayocat.theme.TemplateNotFoundException;
import org.mayocat.theme.Theme;
import org.mayocat.theme.ThemeManager;
import org.mayocat.theme.ThemeResource;
import org.mayocat.theme.UserAgentBreakpointDetector;
import org.mayocat.views.Template;
import org.slf4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.common.io.Resources;

/**
 * @version $Id$
 */
public class DefaultThemeManager implements ThemeManager
{
    private final static String THEMES_FOLDER_NAME = "themes";

    private static final String INDEX_HTML = "index.html";

    @Inject
    private FilesSettings filesSettings;

    @Inject
    private Execution execution;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private UserAgentBreakpointDetector breakpointDetector;

    @Inject
    private Logger logger;

    @Override
    public Template getIndexTemplate(Breakpoint breakpoint) throws TemplateNotFoundException
    {
        try {
            String content = this.getTemplateContent(INDEX_HTML, breakpoint);
            Template template = new Template(generateTemplateId(INDEX_HTML, breakpoint), content);
            return template;
        } catch (IOException e) {
            throw new TemplateNotFoundException(e);
        }
    }

    @Override
    public Template getTemplate(String name, Breakpoint breakpoint) throws TemplateNotFoundException
    {
        try {
            String content = this.getTemplateContent(name, breakpoint);
            Template template = new Template(generateTemplateId(name, breakpoint), content, true);
            return template;
        } catch (IOException e) {
            throw new TemplateNotFoundException(e);
        }
    }

    @Override
    public ThemeResource getResource(String name, Breakpoint breakpoint)
    {
        try {
            ThemeResource result = getResource(getActiveThemeId(), name, breakpoint);
            if (result == null) {
                result = getResource(getDefaultThemeId(), name, breakpoint);
            }
            return result;
        } catch (IOException e) {
            this.logger.warn("I/O Exception while resolving resource []", name, e);
            return null;
        }
    }

    @Override
    public Optional<String> resolveModelPath(String id)
    {
        Map<String, Model> models = getTheme().getModels();
        if (models.containsKey(id)) {
            return Optional.fromNullable(models.get(id).getFile());
        }
        return Optional.absent();
    }

    private String generateTemplateId(String layoutName, Breakpoint breakpoint)
    {
        String themeName = execution.getContext().getTheme().getName();
        String templateId =
                themeName.length() + themeName + "_" + breakpoint.toString().length() + breakpoint.toString()
                        + "_" + layoutName.length() + layoutName;
        return "" + templateId.hashCode();
    }

    private String getTemplateContent(String name, Breakpoint breakpoint) throws TemplateNotFoundException, IOException
    {
        String result = getTemplateContent(getActiveThemeId(), name, breakpoint);
        if (result == null) {
            result = getTemplateContent(getDefaultThemeId(), name, breakpoint);
        }
        if (result == null) {
            throw new TemplateNotFoundException();
        }
        return result;
    }

    private ThemeResource getResource(String theme, String name, Breakpoint breakpoint) throws IOException
    {
        File file;
        String path;

        // 1. Tenant themes folder
        String tenantThemePath =
                filesSettings.getPermanentDirectory() + "/tenants/" + getTenantSlug() + "/" + THEMES_FOLDER_NAME + "/" +
                        theme + "/";

        // 1.1 With breakpoint
        path = tenantThemePath + breakpoint.getFolder() + "/" + name;
        file = new File(path);
        if (file.exists()) {
            return new ThemeResource(ThemeResource.Type.FILE, path);
        }

        // 1.2 Fallback (without breakpoint)
        path = tenantThemePath + name;
        file = new File(path);
        if (file.exists()) {
            return new ThemeResource(ThemeResource.Type.FILE, path);
        }

        // 2. Global
        String globalThemePath = filesSettings.getPermanentDirectory() + "/" + THEMES_FOLDER_NAME + "/" +
                theme + "/";

        // 2.1 With breakpoint
        path = globalThemePath + breakpoint.getFolder() + "/" + name;
        file = new File(path);
        if (file.exists()) {
            return new ThemeResource(ThemeResource.Type.FILE, path);
        }

        // 2.2 Fallback (without breakpoint)
        path = globalThemePath + name;
        file = new File(path);
        if (file.exists()) {
            return new ThemeResource(ThemeResource.Type.FILE, path);
        }

        // 3 Classpath
        String url = THEMES_FOLDER_NAME + "/" + theme + "/";

        // 3.1 With breakpoint
        try {
            path = url + breakpoint + "/" + name;
            Resources.getResource(path);
            return new ThemeResource(ThemeResource.Type.CLASSPATH_RESOURCE, path);
        } catch (IllegalArgumentException e) {
            // keep going...
        }

        // 3.2 Fallback (without breakpoint)
        try {
            path = url + name;
            Resources.getResource(path);
            return new ThemeResource(ThemeResource.Type.CLASSPATH_RESOURCE, path);
        } catch (IllegalArgumentException e) {
            // keep going...
        }

        return null;
    }

    /**
     * Tries to get template content for a certain theme and breakpoint. Tries in order : - tenant theme folder in
     * persistent directory (example : data/tenants/thetenant/themes/thetheme/[breakpoint/]filename - global theme
     * folder in persistent directory (example : data/themes/thetheme/[/breakpoint]filename) - classpath (example uri :
     * /themes/thetheme/[breakpoint/]filename) For each step, checks the breakpoint (if set) and fallback on the
     * no-breakpoint file
     *
     * @param theme the theme for which to try and get the template content
     * @param name the name of the template to get
     * @param breakpoint the breakpoint for which to get the template content
     * @return the content of the template, or null if not found
     * @throws IOException when there is an IO exception getting the content
     */
    private String getTemplateContent(String theme, String name, Breakpoint breakpoint) throws IOException
    {
        ThemeResource resource = this.getResource(theme, name, breakpoint);
        if (resource == null) {
            return null;
        }

        switch (resource.getType()) {
            default:
            case FILE:
                return Files.toString(new File(resource.getPath()), Charsets.UTF_8);
            case CLASSPATH_RESOURCE:
                return Resources.toString(Resources.getResource(resource.getPath()), Charsets.UTF_8);
        }
    }

    private Theme getTheme()
    {
        return execution.getContext().getTheme();
    }

    private String getActiveThemeId()
    {
        ThemeSettings settings = configurationService.getSettings(ThemeSettings.class);
        return settings.getActive().getValue();
    }

    private String getDefaultThemeId()
    {
        ThemeSettings settings = configurationService.getSettings(ThemeSettings.class);
        return settings.getActive().getDefaultValue();
    }

    private String getTenantSlug()
    {
        return execution.getContext().getTenant().getSlug();
    }
}

