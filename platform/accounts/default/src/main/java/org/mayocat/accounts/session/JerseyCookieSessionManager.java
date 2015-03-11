/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.session;

import javax.ws.rs.core.NewCookie;

import org.mayocat.security.EncryptionException;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface JerseyCookieSessionManager
{
    NewCookie[] getCookies(String username, String password, boolean remember) throws EncryptionException;
}
