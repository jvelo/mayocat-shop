/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.multitenancy;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.mayocat.context.WebContext;
import org.mayocat.util.Utils;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * @version $Id$
 */
public class MultitenancyContainerFilter implements ContainerRequestFilter
{
    @Override
    public ContainerRequest filter(ContainerRequest request)
    {
        WebContext context = Utils.getComponent(WebContext.class);

        if (context.getTenant() == null) {
            return request;
        }

        URI requestURI = request.getRequestUri();
        if (requestURI.getPath().indexOf("/api/") == 0 && requestURI.getPath().indexOf("/tenant/") != 0) {
            // API request routed via subdomain

            UriBuilder builder = UriBuilder.fromUri(requestURI);
            builder.replacePath("/tenant/" + context.getTenant().getSlug() + requestURI.getPath());
            request.setUris(request.getBaseUri(), builder.build());
        }

        return request;
    }
}
