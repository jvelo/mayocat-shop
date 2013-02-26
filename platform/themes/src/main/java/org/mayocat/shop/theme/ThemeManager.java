package org.mayocat.shop.theme;

import java.util.List;
import java.util.Map;

import org.mayocat.shop.theme.internal.TemplateNotFoundException;
import org.mayocat.shop.views.Template;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ThemeManager
{
    Template resolveIndex(Breakpoint breakpoint) throws TemplateNotFoundException;

    Template resolve(String name, Breakpoint breakpoint) throws TemplateNotFoundException;

    String resolveLayoutName(String layoutName, Breakpoint breakpoint) throws TemplateNotFoundException;
}
