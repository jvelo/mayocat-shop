/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.api.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTimeZone;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.representations.ResultSetRepresentation;
import org.mayocat.shop.billing.api.representation.OrderRepresentation;
import org.mayocat.shop.billing.meta.OrderEntity;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.yammer.metrics.annotation.Timed;

/**
 * @version $Id$
 */
@Component(OrderResource.PATH)
@Path(OrderResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
public class OrderResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + OrderEntity.PATH;

    @Inject
    private Provider<OrderStore> orderStore;

    @Inject
    private ConfigurationService configurationService;

    @GET
    @Timed
    @Authorized
    public ResultSetRepresentation<OrderRepresentation> getAllOrders(
            @QueryParam("number") @DefaultValue("50") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset)
    {

        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class);
        final DateTimeZone tenantTz = DateTimeZone.forTimeZone(settings.getTime().getTimeZone().getValue());
        List<Order> orders = orderStore.get().findAllPaidOrAwaitingPayment(number, offset);

        Collection<OrderRepresentation> representations =
                Collections2.transform(orders, new Function<Order, OrderRepresentation>()
                {
                    public OrderRepresentation apply(final Order order)
                    {
                        return new OrderRepresentation(tenantTz, order);
                    }
                });

        Integer total = this.orderStore.get().countAll();
        ResultSetRepresentation<OrderRepresentation> resultSet = new ResultSetRepresentation<OrderRepresentation>(
                PATH,
                number,
                offset,
                new ArrayList<OrderRepresentation>(representations),
                total
        );

        return resultSet;
    }

    @GET
    @Path("{slug}")
    @Authorized
    public Response getOrder(@PathParam("slug") String slug)
    {
        Order order = orderStore.get().findBySlug(slug);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Order not found").build();
        }
        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class);
        final DateTimeZone tenantTz = DateTimeZone.forTimeZone(settings.getTime().getTimeZone().getValue());
        return Response.ok(new OrderRepresentation(tenantTz, order)).build();
    }

    @Path("{slug}")
    @POST
    @Timed
    @Authorized
    // Partial update : NOT idempotent
    public Response updateOrder(@PathParam("slug") String slug,
            OrderRepresentation updatedOrderRepresentation)
    {
        try {
            Order order = this.orderStore.get().findBySlug(slug);
            if (order == null) {
                return Response.status(404).build();
            } else {
                order.setStatus(updatedOrderRepresentation.getStatus());
                this.orderStore.get().update(order);
                return Response.ok().build();
            }
        } catch (InvalidEntityException e) {
            throw new com.yammer.dropwizard.validation.InvalidEntityException(e.getMessage(), e.getErrors());
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No product with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }
}
