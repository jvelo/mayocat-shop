/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.jersey;

import javax.ws.rs.core.Response;

import org.mayocat.rest.CorsSettings;
import org.mayocat.util.Utils;

import com.google.common.base.Strings;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * @version $Id$
 */
public class CorsResponseFilter implements ContainerResponseFilter
{
    @Override
    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse)
    {
        Response.ResponseBuilder response = Response.fromResponse(containerResponse.getResponse());
        CorsSettings corsSettings = Utils.getComponent(CorsSettings.class);

        if (corsSettings.isEnabled()) {
            response.header("Access-Control-Allow-Origin", corsSettings.getAllowOrigin())
                    .header("Access-Control-Allow-Methods", corsSettings.getAllowMethods());

            if (corsSettings.isAllowCredentials()) {
                response.header("Access-Control-Allow-Credentials", "true");
            }

            String authorizedHeaders = corsSettings.getAllowHeaders();
            String requestedHeaders = containerRequest.getHeaderValue("Access-Control-Request-Headers");
            if (corsSettings.isCopyRequestedHeaders() && !Strings.isNullOrEmpty(requestedHeaders)) {
                authorizedHeaders.concat(", ").concat(requestedHeaders);
            }

            if (!Strings.isNullOrEmpty(authorizedHeaders)) {
                response.header("Access-Control-Allow-Headers", authorizedHeaders);
            }

            if (!Strings.isNullOrEmpty(corsSettings.getExposeHeaders())) {
                response.header("Access-Control-Expose-Headers", corsSettings.getExposeHeaders());
            }
        }

        containerResponse.setResponse(response.build());
        return containerResponse;
    }
}
