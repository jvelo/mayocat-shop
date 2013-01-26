package org.mayocat.shop.rest.jersey;

import org.mayocat.shop.theme.Breakpoint;
import org.mayocat.shop.theme.UserAgentBreakpointDetector;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;

/**
 * @version $Id$
 */
public class BreakpointInjectable extends AbstractHttpContextInjectable<Breakpoint>
{
    private UserAgentBreakpointDetector detector;

    public BreakpointInjectable(UserAgentBreakpointDetector detector)
    {
        this.detector = detector;
    }

    @Override
    public Breakpoint getValue(HttpContext context)
    {
        return this.detector.getBreakpoint(context.getRequest().getHeaderValue("User-Agent"));
    }
}
