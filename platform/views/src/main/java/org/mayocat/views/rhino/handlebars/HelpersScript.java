package org.mayocat.views.rhino.handlebars;

import java.nio.file.Path;

import org.xwiki.component.annotation.Role;

/**
 * Represents a script that declares helpers for the handlebars.js front views contexts.
 *
 * Note: the role-hint of implementation is used as script name.
 *
 * @version $Id$
 */
@Role
public interface HelpersScript
{
    Path getPath();
}
