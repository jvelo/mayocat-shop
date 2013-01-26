package org.mayocat.shop.rest.jersey;

import javax.inject.Inject;
import javax.ws.rs.core.Context;

import org.mayocat.shop.base.Provider;
import org.mayocat.shop.theme.UserAgentBreakpointDetector;
import org.xwiki.component.annotation.Component;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * @version $Id$
 */
@Component("breakpoint")
public class BreakpointProvider implements InjectableProvider<Context, Parameter>, Provider
{
    @Inject
    private UserAgentBreakpointDetector breakpointDetector;

    @Override
    public ComponentScope getScope()
    {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, Context breakpoint, Parameter parameter)
    {
        return new BreakpointInjectable(breakpointDetector);
    }
}
