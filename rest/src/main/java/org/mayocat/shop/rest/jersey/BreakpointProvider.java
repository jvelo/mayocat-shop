package org.mayocat.shop.rest.jersey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.core.Context;

import org.mayocat.shop.base.Provider;
import org.mayocat.shop.theme.annotation.Breakpoint;
import org.mayocat.shop.theme.UserAgentBreakpointDetector;
import org.xwiki.component.annotation.Component;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * @version $Id$
 */
@Component("breakpoint")
public class BreakpointProvider implements InjectableProvider<Breakpoint, Parameter>, Provider
{
    @Inject
    private UserAgentBreakpointDetector breakpointDetector;

    @Override
    public ComponentScope getScope()
    {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, Breakpoint breakpoint, Parameter parameter)
    {
        return new BreakpointInjectable(breakpointDetector);
    }
}
