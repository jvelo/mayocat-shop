package org.mayocat.context;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.mayocat.configuration.SecuritySettings;
import org.mayocat.security.Cipher;
import org.mayocat.security.EncryptionException;
import org.mayocat.session.Session;
import org.mayocat.session.WebScope;
import org.mayocat.session.cookies.CookieSession;
import org.mayocat.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

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
    protected Session getScope(Execution execution)
    {
        return execution.getContext().getSession();
    }

    @Override
    protected void setScope(Execution execution, Session session)
    {
        execution.getContext().setSession(session);
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
    protected boolean scopeExistsAndNotEmpty(Execution execution)
    {
        return execution.getContext() != null && execution.getContext().getSession() != null &&
                !execution.getContext().getSession().isEmpty();
    }
}
