package org.mayocat.theme;

import org.mayocat.theme.TemplateNotFoundException;
import org.mayocat.views.Template;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ThemeManager
{
    Template resolveIndex(Breakpoint breakpoint) throws TemplateNotFoundException;

    Template resolve(String name, Breakpoint breakpoint) throws TemplateNotFoundException;
}
