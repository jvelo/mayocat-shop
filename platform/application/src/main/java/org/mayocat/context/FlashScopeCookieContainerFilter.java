package org.mayocat.context;

import org.mayocat.context.scope.Flash;
import org.mayocat.context.scope.cookie.CookieFlash;

import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Flash scope container filter. It is not encrypted.
 *
 * @version $Id$
 */
public class FlashScopeCookieContainerFilter extends AbstractScopeCookieContainerFilter<Flash>
        implements ContainerResponseFilter, ContainerRequestFilter
{
    @Override
    protected String getScopeAndCookieName()
    {
        return "flash";
    }

    @Override
    protected boolean scopeExistsAndNotEmpty(WebContext context)
    {
        return context != null && context.getFlash() != null && !context.getFlash().isEmpty();
    }

    @Override
    protected Flash getScope(WebContext context)
    {
        return context.getFlash();
    }

    @Override
    protected void setScope(WebContext context, Flash scope)
    {
        context.setFlash(scope);
    }

    @Override
    protected boolean encryptAndSign()
    {
        return false;
    }

    @Override
    protected Flash cast(Object object)
    {
        CookieFlash flash = (CookieFlash) object;
        flash.consume();
        return flash;
    }
}
