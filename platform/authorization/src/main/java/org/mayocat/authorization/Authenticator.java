/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.authorization;

import org.mayocat.authorization.cookies.CookieAuthenticator;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.xwiki.component.annotation.Role;

import com.google.common.base.Optional;

/**
 * An authenticator is capable of authentifying request based on the value of a certain header. Each authenticator only
 * respond to certain types of header name/value pairs.
 * For example, a {@link org.mayocat.authorization.basic.BasicAuthenticator} will only accept to
 * respond to the authentication challenge when passed a header "Authorization" with a value that starts with "Basic".
 * Similarly, a {@link CookieAuthenticator} will only accept to respond when passed a "Cookie" header and a value that
 * contains a certain number of cookies it needs (encrypted username and password, etc). Implementation must specify
 * which header name/value pair they accept to respond to in the {@link #respondTo(String, String)} method.
 * Authentication verification logic should then be implemented in the {@link #verify(String)} method, where the value
 * passed is always a header value the authenticator has accepted to respond to.
 * 
 * @version $Id$
 */
@Role
public interface Authenticator
{
    /**
     * @param headerName the name of the header this authenticator has to accept or refuse to respond to
     * @param headerValue the value of the header this authenticator has to accept or refuse to respond to
     * @return true if this authenticator accepts to respond to this challenge, false otherwise
     */
    boolean respondTo(String headerName, String headerValue);

    /**
     * @param value the header value to authenticate
     * @param tenant the tenant for which to verify the header
     * @return an empty option when the authenticator did not authentify any user against the challenge, an option with
     *         the authenticated user otherwise.
     */
    Optional<User> verify(String value, Tenant tenant);
}
