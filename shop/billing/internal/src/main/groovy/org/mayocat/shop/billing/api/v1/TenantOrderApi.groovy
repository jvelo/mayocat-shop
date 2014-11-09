/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.api.v1

import groovy.transform.CompileStatic
import org.joda.time.DateTimeZone
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.api.object.Pagination
import org.mayocat.shop.billing.api.representation.OrderRepresentation
import org.mayocat.shop.billing.api.v1.object.OrderApiObject
import org.mayocat.shop.billing.api.v1.object.OrderListApiObject
import org.mayocat.shop.billing.model.Order
import org.mayocat.shop.billing.store.OrderStore
import org.mayocat.shop.payment.model.PaymentOperation
import org.mayocat.shop.payment.store.PaymentOperationStore
import org.mayocat.store.EntityDoesNotExistException
import org.mayocat.store.InvalidEntityException
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@CompileStatic @Component("/tenant/{tenant}/api/orders")
@Path("/tenant/{tenant}/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
class TenantOrderApi implements Resource
{
    @Inject
    Provider<OrderStore> orderStore

    @Inject
    ConfigurationService configurationService

    @Inject
    Provider<PaymentOperationStore> paymentOperationStore

    @Inject
    WebContext context

    @GET
    @Authorized
    def getAllOrders(@QueryParam("number") @DefaultValue("50") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset)
    {
        if (number < 0 || offset < 0) {
            return Response.status(Response.Status.BAD_REQUEST).build()
        }

        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class)
        final DateTimeZone tenantTz = DateTimeZone.forTimeZone(settings.getTime().getTimeZone().getValue())
        List<Order> orders = orderStore.get().findAllPaidOrAwaitingPayment(number, offset)
        Integer total = this.orderStore.get().countAllPaidOrAwaitingPayment()

        List<OrderApiObject> orderApiObjects = orders.collect({ Order order ->
            def apiObject = new OrderApiObject().withOrder(order, tenantTz)
            if (order.customer.isLoaded()) {
                apiObject.withEmbeddedCustomer(order.customer.get())
            }
            if (order.billingAddress.isLoaded()) {
                apiObject.withEmbeddedBillingAddress(order.billingAddress.get())
            }
            if (order.deliveryAddress.isLoaded()) {
                apiObject.withEmbeddedDeliveryAddress(order.deliveryAddress.get())
            }

            apiObject
        })

        new OrderListApiObject([
                _pagination: new Pagination([
                        numberOfItems: number,
                        returnedItems: orderApiObjects.size(),
                        offset       : offset,
                        totalItems   : total,
                        urlTemplate  : '${tenantPrefix}/api/orders?number=${numberOfItems}&offset=${offset}',
                        urlArguments : [
                                tenantPrefix: context.request.tenantPrefix
                        ]
                ]),
                orders     : orderApiObjects
        ])
    }

    @GET
    @Path("{slug}")
    @Authorized
    def getOrder(@PathParam("slug") String slug)
    {
        Order order = orderStore.get().findBySlug(slug)
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Order not found").build()
        }

        List<PaymentOperation> operations = paymentOperationStore.get().findAllForOrderId(order.id)
        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class)
        final DateTimeZone tenantTz = DateTimeZone.forTimeZone(settings.getTime().getTimeZone().getValue())

        def apiObject = new OrderApiObject().withOrder(order, tenantTz)
        apiObject.withEmbeddedPaymentOperations(operations)

        if (order.customer.isLoaded()) {
            apiObject.withEmbeddedCustomer(order.customer.get())
        }
        if (order.billingAddress.isLoaded()) {
            apiObject.withEmbeddedBillingAddress(order.billingAddress.get())
        }
        if (order.deliveryAddress.isLoaded()) {
            apiObject.withEmbeddedDeliveryAddress(order.deliveryAddress.get())
        }

        return Response.ok(apiObject).build()
    }

    @Path("{slug}")
    @POST
    @Authorized
    // Partial update : NOT idempotent
    def updateOrder(@PathParam("slug") String slug,
            OrderApiObject orderApiObject)
    {
        try {
            Order order = this.orderStore.get().findBySlug(slug)
            if (order == null) {
                return Response.status(404).build()
            } else {
                order.setStatus(orderApiObject.getStatus())
                this.orderStore.get().update(order)
                return Response.ok().build()
            }
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No product with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build()
        }
    }
}
