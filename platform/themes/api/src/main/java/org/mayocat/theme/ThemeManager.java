package org.mayocat.theme;

import java.io.IOException;

import org.mayocat.accounts.model.Tenant;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ThemeManager
{
    Theme getTheme();

    Theme getTheme(Tenant tenant);
}
