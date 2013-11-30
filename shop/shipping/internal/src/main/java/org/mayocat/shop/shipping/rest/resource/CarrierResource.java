/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping.rest.resource;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.rest.Resource;
import org.mayocat.shop.shipping.Strategy;
import org.mayocat.shop.shipping.model.Carrier;
import org.mayocat.shop.shipping.store.CarrierStore;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("/api/shipping/carrier/")
@Path("/api/shipping/carrier/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarrierResource implements Resource
{
    @Inject
    private CarrierStore carrierStore;

    @Inject
    private ConfigurationService configurationService;

    @GET
    public Response getCarriers(@QueryParam("strategy") String strategy)
    {
        if (strategy != null) {
            return Response.ok().entity(carrierStore.findAll(Strategy.fromJson(strategy))).build();
        } else {
            return Response.ok().entity(carrierStore.findAll()).build();
        }
    }

    @POST
    public Response createCarrier(Carrier carrier)
    {
        this.carrierStore.createCarrier(carrier);
        return Response.ok().build();
    }

    @PUT
    @Path("{id}")
    public Response updateCarrier(@PathParam("id") String id, Carrier carrier)
    {
        if (this.carrierStore.findById(UUID.fromString(id)) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        carrier.setId(UUID.fromString(id));
        this.carrierStore.updateCarrier(carrier);
        return Response.ok().build();
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.WILDCARD)
    public Response deleteCarrier(@PathParam("id") String id)
    {
        Carrier carrier = this.carrierStore.findById(UUID.fromString(id));
        if (carrier == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        this.carrierStore.deleteCarrier(carrier);
        return Response.noContent().build();
    }
}
