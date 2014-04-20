/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.security;

import javax.inject.Inject;

import org.mayocat.configuration.SecuritySettings;
import org.mindrot.jbcrypt.BCrypt;
import org.xwiki.component.annotation.Component;

@Component(hints = {"bcrypt", "default"})
public class BCryptPasswordManager implements PasswordManager
{

    @Inject
    private SecuritySettings configuration;
    
    public String hashPassword(String password)
    {
        return BCrypt.hashpw(password, BCrypt.gensalt(configuration.getPasswordSaltLogRounds()));
    }

    public boolean verifyPassword(String candidate, String hashed)
    {
        return BCrypt.checkpw(candidate, hashed);
    }

}
