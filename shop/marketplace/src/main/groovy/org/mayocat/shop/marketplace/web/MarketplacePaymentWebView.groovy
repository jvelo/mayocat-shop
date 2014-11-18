/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web

import com.google.common.base.Strings
import groovy.transform.CompileStatic
import org.mayocat.shop.payment.GatewayException
import org.mayocat.shop.payment.GatewayFactory
import org.mayocat.shop.payment.GatewayResponse
import org.mayocat.shop.payment.PaymentGateway
import org.mayocat.shop.payment.api.resources.PaymentResource
import org.mayocat.shop.payment.event.PaymentOperationEvent
import org.mayocat.shop.payment.model.PaymentOperation
import org.mayocat.shop.payment.store.PaymentOperationStore
import org.mayocat.store.EntityAlreadyExistsException
import org.mayocat.store.InvalidEntityException
import org.slf4j.Logger
import org.xwiki.component.annotation.Component
import org.xwiki.observation.ObservationManager

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("marketplace/payment")
@Path("marketplace/payment")
@Produces(MediaType.WILDCARD)
@Consumes(MediaType.WILDCARD)
@CompileStatic
class MarketplacePaymentWebView extends PaymentResource
{
    @Inject
    Map<String, GatewayFactory> gatewayFactories;

    @Inject
    Logger logger

    @Inject
    Provider<PaymentOperationStore> paymentOperationStore;

    @Inject
    ObservationManager observationManager;

    @POST
    @Path("acknowledgement/{gatewayId}")
    public Response acknowledgePayment(@PathParam("gatewayId") String gatewayId,
            MultivaluedMap<String, String> data)
    {
        GatewayFactory factory = gatewayFactories.get(gatewayId);
        PaymentGateway gateway = factory.createGateway();
        GatewayResponse response;

        try {
            response = gateway.acknowledge(data);
            PaymentOperation op = response.getOperation();

            if (op.orderId) {
                op.setOrderId(op.orderId);
                paymentOperationStore.get().create(op);
                observationManager.notify(new PaymentOperationEvent(), op);
            } else {
                logger.warn("Payment acknowledgement did not return an order ID. Can't process");
            }
        } catch (GatewayException | InvalidEntityException | EntityAlreadyExistsException e) {
            this.logger.error("Failed to acknowledge payment", e);
            throw new WebApplicationException(e);
        }

        if (!Strings.isNullOrEmpty(response.getResponseText())) {
            return Response.ok(response.getResponseText()).build();
        }

        return Response.ok().build();
    }

    @POST
    @Path("callback/{gatewayId}")
    public Response genericCallback(@PathParam("gatewayId") String gatewayId,
            MultivaluedMap<String, String> data)
    {
        GatewayFactory factory = gatewayFactories.get(gatewayId);
        PaymentGateway gateway = factory.createGateway();
        GatewayResponse response;

        try {
            response = gateway.callback(data);
            PaymentOperation op = response.getOperation();

            if (op) {
                paymentOperationStore.get().create(op);
                observationManager.notify(new PaymentOperationEvent(), op);
            }
        } catch (GatewayException | InvalidEntityException | EntityAlreadyExistsException e) {
            this.logger.error("Error during gateway callback", e);
            throw new WebApplicationException(e);
        }

        if (!Strings.isNullOrEmpty(response.getResponseText())) {
            return Response.ok(response.getResponseText()).build();
        }

        return Response.ok().build();
    }
}
