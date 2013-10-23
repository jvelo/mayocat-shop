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
