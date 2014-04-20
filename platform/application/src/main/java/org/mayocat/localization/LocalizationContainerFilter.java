/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.localization;

import java.net.URI;
import java.util.Locale;

import javax.ws.rs.core.UriBuilder;

import org.mayocat.application.AbstractService;
import org.mayocat.context.WebContext;
import org.mayocat.util.Utils;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * Localization container filter that strips out the language tag if present in the URI before its passed to jersey
 * resources.
 *
 * FIXME: this should be in the localization module but there's a dependency on AbstractService that would introduce
 * a cyclic dependency chain. The proper way to resolve this would be to introduce a component that can resolves if a
 * certain path is an static path, or API path or Web path...
 *
 * @version $Id$
 */
public class LocalizationContainerFilter implements ContainerRequestFilter
{
    @Override
    public ContainerRequest filter(ContainerRequest containerRequest)
    {
        if (isStaticPath(containerRequest.getRequestUri().getPath())) {
            return containerRequest;
        }

        WebContext context = Utils.getComponent(WebContext.class);

        if (context.getTenant() == null) {
            return containerRequest;
        }

        if (context.isAlternativeLocale()) {
            URI requestURI = containerRequest.getRequestUri();
            Locale locale = context.getLocale();
            UriBuilder builder = UriBuilder.fromUri(requestURI);
            builder.replacePath(requestURI.getPath().substring(locale.toString().length() + 1));
            containerRequest.setUris(containerRequest.getBaseUri(), builder.build());
        }

        return containerRequest;
    }

    private boolean isStaticPath(String path)
    {
        for (String staticPath : AbstractService.getStaticPaths()) {
            if (path.startsWith(staticPath)) {
                return true;
            }
        }
        return false;
    }
}
