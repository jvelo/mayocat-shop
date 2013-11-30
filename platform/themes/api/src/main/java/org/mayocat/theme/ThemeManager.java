/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
