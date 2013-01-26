package org.mayocat.shop.theme.internal;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.shop.configuration.theme.ThemeConfiguration;
import org.mayocat.shop.theme.Breakpoint;
import org.mayocat.shop.theme.ThemeManager;
import org.mayocat.shop.theme.UserAgentBreakpointDetector;
import org.mayocat.shop.views.Template;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Resources;

/**
 * @version $Id$
 */
public class DefaultThemeManager implements ThemeManager
{
    // Configurable ?
    private final static String THEMES_ROOT_FOLDER = "themes";

    @Inject
    private ThemeConfiguration themeConfiguration;

    @Inject
    private UserAgentBreakpointDetector breakpointDetector;

    @Override
    public Template resolveIndex(Breakpoint breakpoint) throws TemplateNotFoundException
    {
        String name = "index.html";
        String path = this.resolveTemplatePath(name, breakpoint);
        String content = null;
        try {
            content = Resources.toString(Resources.getResource(path), Charsets.UTF_8);
            Template template = new Template(path, content);
            return template;
        } catch (IOException e) {
            throw new TemplateNotFoundException(e);
        }
    }
    @Override
    public Template resolve(String name, Breakpoint breakpoint) throws TemplateNotFoundException
    {
        String path = this.resolveTemplatePath(name, breakpoint);
        String content = null;
        try {
            content = Resources.toString(Resources.getResource(path), Charsets.UTF_8);
            Template template = new Template(path, content, true);
            return template;
        } catch (IOException e) {
            throw new TemplateNotFoundException(e);
        }
    }

    @Override
    public String resolveLayoutName(String layoutName, Breakpoint breakpoint) throws TemplateNotFoundException
    {
        return this.resolveTemplatePath(layoutName, breakpoint);
    }

    private String resolveTemplatePath(String name, Breakpoint breakpoint) throws TemplateNotFoundException
    {
        if (!Strings.isNullOrEmpty(breakpoint.getFolder())) {
            String url =
                    THEMES_ROOT_FOLDER + "/" + themeConfiguration.getActive().getValue() + "/" + breakpoint.getFolder() + "/" +
                            name;
            try {
                Resources.getResource(url);
                return url;
            }
            catch (IllegalArgumentException e) {
                // resource not found
                // fallback on default breakpoint
            }
        }
        String url = THEMES_ROOT_FOLDER + "/" + themeConfiguration.getActive().getValue() + "/" + name;
        try {
            Resources.getResource(url);
            return url;
        } catch (IllegalArgumentException e) {
            throw new TemplateNotFoundException();
        }
    }
}

