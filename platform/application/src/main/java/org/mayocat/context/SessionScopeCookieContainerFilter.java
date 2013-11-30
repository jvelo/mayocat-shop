/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.context;

import org.mayocat.context.scope.Session;
import org.mayocat.context.scope.cookie.CookieSession;

/**
 * @version $Id$
 */
public class SessionScopeCookieContainerFilter extends AbstractScopeCookieContainerFilter<Session>

{
    @Override
    protected int getCookieDuration()
    {
        // TODO
        // Make it infinite by default, but configurable
        return 60 * 60 * 24 * 15;
    }

    @Override
    protected String getScopeAndCookieName()
    {
        return "session";
    }

    @Override
    protected Session getScope(WebContext context)
    {
        return context.getSession();
    }

    @Override
    protected void setScope(WebContext context, Session session)
    {
        context.setSession(session);
    }

    @Override
    protected boolean encryptAndSign()
    {
        return true;
    }

    @Override
    protected Session cast(Object object)
    {
        return (CookieSession) object;
    }

    @Override
    protected boolean scopeExistsAndNotEmpty(WebContext context)
    {
        return context != null && context.getSession() != null && !context.getSession().isEmpty();
    }
}
