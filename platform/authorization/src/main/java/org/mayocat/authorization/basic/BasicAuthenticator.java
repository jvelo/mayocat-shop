/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.authorization.basic;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.jetty.util.B64Code;
import org.eclipse.jetty.util.StringUtil;
import org.mayocat.authorization.Authenticator;
import org.mayocat.security.PasswordManager;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.accounts.store.UserStore;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.net.HttpHeaders;

@Component("basic")
public class BasicAuthenticator implements Authenticator
{
    @Inject
    private Provider<UserStore> userStore;

    @Inject
    private PasswordManager passwordManager;

    @Inject
    private Logger logger;

    private final static String METHOD = "Basic";

    @Override
    public boolean respondTo(String headerName, String headerValue)
    {
        if (headerName.equalsIgnoreCase(HttpHeaders.AUTHORIZATION)) {
            final int space = headerValue.indexOf(' ');
            if (space > 0) {
                final String method = headerValue.substring(0, space);
                if (method.equalsIgnoreCase(METHOD)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Optional<User> verify(String value, Tenant tenant)
    {
        final int space = value.indexOf(' ');
        if (space > 0) {
            try {
                final String decoded = B64Code.decode(value.substring(space + 1), StringUtil.__ISO_8859_1);
                final int i = decoded.indexOf(':');
                if (i > 0) {
                    final String username = decoded.substring(0, i);
                    final String password = decoded.substring(i + 1);
                    User user = userStore.get().findUserByEmailOrUserName(username);
                    if (user != null) {
                        if (this.passwordManager.verifyPassword(password, user.getPassword())) {
                            return Optional.of(user);
                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {
                this.logger.debug("Failed to decode basic auth credentials");
            }
        }
        return Optional.absent();
    }

}
