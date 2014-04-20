/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping.rest.resource;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.rest.Resource;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * @version $Id$
 */
@Component("/api/shipping/destinations/")
@Path("/api/shipping/destinations/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DestinationsResource implements Resource
{
    @Inject
    private Logger logger;

    @GET
    public Response getDestinations()
    {
        try {
            return Response.ok(
                    Resources.toString(Resources.getResource("org/mayocat/shop/shipping/destinations/earth.json"),
                            Charsets.UTF_8))
                    .build();
        } catch (IOException e) {
            this.logger.error("Failed to get location file", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("flat")
    public Response getDestinationsFlat()
    {
        try {
            return Response.ok(
                    Resources.toString(Resources.getResource("org/mayocat/shop/shipping/destinations/earth_flat.json"),
                            Charsets.UTF_8))
                    .build();
        } catch (IOException e) {
            this.logger.error("Failed to get location file", e);
            return Response.serverError().build();
        }
    }
}
