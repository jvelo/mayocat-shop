package org.mayocat.theme.internal;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.mayocat.configuration.general.FilesSettings;
import org.mayocat.configuration.theme.ThemeSettings;
import org.mayocat.context.Execution;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.TemplateNotFoundException;
import org.mayocat.theme.ThemeManager;
import org.mayocat.theme.UserAgentBreakpointDetector;
import org.mayocat.views.Template;

import com.google.common.base.Charsets;
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
    private ThemeSettings themeSettings;

    @Inject
    private UserAgentBreakpointDetector breakpointDetector;

    @Override
    public Template resolveIndex(Breakpoint breakpoint) throws TemplateNotFoundException
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
    public Template resolve(String name, Breakpoint breakpoint) throws TemplateNotFoundException
    {
        try {
            String content = this.getTemplateContent(name, breakpoint);
            Template template = new Template(generateTemplateId(name, breakpoint), content, true);
            return template;
        } catch (IOException e) {
            throw new TemplateNotFoundException(e);
        }
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
        String result = getTemplateContent(getActiveTheme(), name, breakpoint);
        if (result == null) {
            result = getTemplateContent(getDefaultTheme(), name, breakpoint);
        }
        if (result == null) {
            throw new TemplateNotFoundException();
        }
        return result;
    }

    /**
     * Tries to get template content for a certain theme and breakpoint. Tries in order :
     * - tenant theme folder in persistent directory (example : data/tenants/thetenant/themes/thetheme/[breakpoint/]filename
     * - global theme folder in persistent directory (example : data/themes/thetheme/[/breakpoint]filename)
     *  - classpath (example uri : /themes/thetheme/[breakpoint/]filename)
     *  For each step, checks the breakpoint (if set) and fallback on the no-breakpoint file
     *
     * @param theme the theme for which to try and get the template content
     * @param name the name of the template to get
     * @param breakpoint the breakpoint for which to get the template content
     * @return the content of the template, or null if not found
     * @throws IOException when there is an IO exception getting the content
     */
    private String getTemplateContent(String theme, String name, Breakpoint breakpoint) throws IOException
    {
        File file;

        // 1. Tenant themes folder
        String tenantThemePath =
                filesSettings.getPermanentDirectory() + "/tenants/" + getTenantSlug() + "/" + THEMES_FOLDER_NAME + "/" +
                        theme + "/";

        // 1.1 With breakpoint
        file = new File(tenantThemePath + breakpoint.getFolder() + "/" + name);
        if (file.exists()) {
            return Files.toString(file, Charsets.UTF_8);
        }

        // 1.2 Fallback (without breakpoint)
        file = new File(tenantThemePath + name);
        if (file.exists()) {
            return Files.toString(file, Charsets.UTF_8);
        }

        // 2. Global
        String globalThemePath = filesSettings.getPermanentDirectory() + "/" + THEMES_FOLDER_NAME + "/" +
                theme + "/";

        // 2.1 With breakpoint
        file = new File(globalThemePath + breakpoint.getFolder() + "/" + name);
        if (file.exists()) {
            return Files.toString(file, Charsets.UTF_8);
        }

        // 2.2 Fallback (without breakpoint)
        file = new File(globalThemePath + name);
        if (file.exists()) {
            return Files.toString(file, Charsets.UTF_8);
        }

        // 3 Classpath
        String url = THEMES_FOLDER_NAME + "/" + theme + "/";

        // 3.1 With breakpoint
        try {
            return Resources.toString(Resources.getResource(url + breakpoint + "/" + name), Charsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // keep going...
        }

        // 3.2 Fallback (without breakpoint)
        try {
            return Resources.toString(Resources.getResource(url + "/" + name), Charsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // keep going...
        }

        return null;
    }

    private String getActiveTheme()
    {
        return themeSettings.getActive().getValue();
    }

    private String getDefaultTheme()
    {
        return themeSettings.getActive().getDefaultValue();
    }

    private String getTenantSlug()
    {
        return execution.getContext().getTenant().getSlug();
    }
}

