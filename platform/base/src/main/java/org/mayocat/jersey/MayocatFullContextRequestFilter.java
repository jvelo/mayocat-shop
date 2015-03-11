/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.jersey;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Jersey container filter that copies over the X-Mayocat-Full-Context flag header from the request to the response
 * when it is present.
 *
 * @version $Id$
 */
public class MayocatFullContextRequestFilter implements ContainerResponseFilter
{
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response)
    {
        if (request.getRequestHeader("X-Mayocat-Full-Context") != null) {
            response.getHttpHeaders().putSingle("X-Mayocat-Full-Context", true);
        }
        return response;
    }
}
