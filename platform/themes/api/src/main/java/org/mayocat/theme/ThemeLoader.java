package org.mayocat.theme;

import java.io.IOException;

import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ThemeLoader
{
    Theme load(String name) throws IOException;
}
