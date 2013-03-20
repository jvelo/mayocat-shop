package org.mayocat.theme;

import org.mayocat.theme.TemplateNotFoundException;
import org.mayocat.views.Template;
import org.xwiki.component.annotation.Role;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
@Role
public interface ThemeManager
{
    Template resolveIndexTemplate(Breakpoint breakpoint) throws TemplateNotFoundException;

    Template resolveTemplate(String name, Breakpoint breakpoint) throws TemplateNotFoundException;

    Optional<String> resolveModelPath(String id);
}
