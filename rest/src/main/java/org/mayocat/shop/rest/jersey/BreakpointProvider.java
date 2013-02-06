package org.mayocat.shop.rest.jersey;

import java.lang.reflect.Type;

import javax.inject.Inject;

import org.mayocat.shop.base.Provider;
import org.mayocat.shop.theme.Breakpoint;
import org.mayocat.shop.theme.UserAgentBreakpointDetector;
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
