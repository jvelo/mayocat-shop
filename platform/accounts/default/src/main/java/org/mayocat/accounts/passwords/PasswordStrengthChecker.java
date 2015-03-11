/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.passwords;

import org.xwiki.component.annotation.Role;

/**
 * A password strength checker verifies that passwords match security strength requirements.
 *
 * @version $Id$
 */
@Role
public interface PasswordStrengthChecker
{
    /**
     * Checks that the passed passwords complies to the application length requirements for user accounts.
     *
     * @param password the password to check against the length requirements
     * @return true if the password passes the requirement test, false otherwise
     */
    boolean checkLength(String password);

    /**
     * Checks that the passed passwords complies to the application minimum bits of entropy requirements
     *
     * @param password the password to check against the entropy requirement
     * @return true if the password passes the requirement test, false otherwise
     */
    boolean checkEntropy(String password);
}
