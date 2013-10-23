package org.mayocat.context;

import org.mayocat.session.Flash;
import org.mayocat.session.cookies.CookieFlash;

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
    protected boolean scopeExistsAndNotEmpty(Execution execution)
    {
        return execution.getContext() != null && execution.getContext().getFlash() != null &&
                !execution.getContext().getFlash().isEmpty();
    }

    @Override
    protected Flash getScope(Execution execution)
    {
        return execution.getContext().getFlash();
    }

    @Override
    protected void setScope(Execution execution, Flash scope)
    {
        execution.getContext().setFlash(scope);
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
