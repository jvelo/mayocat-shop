/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.security;

import org.xwiki.component.annotation.Role;

/**
 * A password manager has the responsibility of hashing passwords and verifying a clear password against an existing
 * hash.
 * 
 * @version $Id$
 */
@Role
public interface PasswordManager
{
    /**
     * @param password the password to hash
     * @return the hashed version of the password
     */
    String hashPassword(String password);

    /**
     * @param candidate the clear-text password to verify
     * @param hashed the knowned valid hashed password to verify against
     * @return true if the hash of the clear-text password matches the known valid hash
     */
    boolean verifyPassword(String candidate, String hashed);
}
