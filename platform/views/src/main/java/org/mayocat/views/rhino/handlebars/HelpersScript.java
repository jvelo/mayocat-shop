/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
