/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.jersey;

import javax.inject.Inject;

import org.mayocat.rest.Provider;
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
        return this.breakpointDetector.getBreakpoint(httpContext.getRequest().getHeaderValue("User-Agent")).orNull();
    }
}
