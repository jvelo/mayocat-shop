package org.mayocat.shop.rest.jersey;

import javax.inject.Inject;

import org.mayocat.base.Provider;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.UserAgentBreakpointDetector;
import org.xwiki.component.annotation.Component;

import com.sun.jersey.api.core.HttpContext;

/**
 * @version $Id$
 */
@Component("breakpoint")
public class BreakpointProvider extends AbstractInjectableProvider<Breakpoint> implements Provider
{
    @Inject
    private UserAgentBreakpointDetector breakpointDetector;

    public BreakpointProvider()
    {
        super(Breakpoint.class);
    }

    @Override
    public Breakpoint getValue(HttpContext httpContext)
    {
        return this.breakpointDetector.getBreakpoint(httpContext.getRequest().getHeaderValue("User-Agent"));
    }
}
