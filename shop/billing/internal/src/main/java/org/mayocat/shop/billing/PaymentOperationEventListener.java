/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.billing.event.OrderPaidEvent;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.shop.payment.event.PaymentOperationEvent;
import org.mayocat.shop.payment.model.PaymentOperation;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.ObservationManager;
import org.xwiki.observation.event.Event;

/**
 * An event listener that respond to payment operation events.
 *
 * When a payment operation with result "CAPTURED" is received, this listener updates the status of the concerned
 * order.
 *
 * @version $Id$
 */
@Component("billingPaymentOperationListener")
public class PaymentOperationEventListener implements EventListener
{
    /**
     * The store to access to orders
     */
    @Inject
    private Provider<OrderStore> orderStore;

    /**
     * The one with the chain-saw
     */
    @Inject
    private Logger logger;

    /**
     * Component manager, used to access the {@link #observationManager}.
     */
    @Inject
    private ComponentManager componentManager;

    /**
     * The observation manager used to notify events. Not injected, as it would create a cycling dependency with this
     * event component.
     */
    private ObservationManager observationManager;

    @Override
    public String getName()
    {
        return "billingPaymentOperationListener";
    }

    @Override
    public List<Event> getEvents()
    {
        return Arrays.<Event>asList(new PaymentOperationEvent());
    }

    @Override
    public void onEvent(Event event, Object source, Object data)
    {
        this.logger.debug("Payment operation event received");

        PaymentOperation operation = (PaymentOperation) source;

        if (operation.getResult().equals(PaymentOperation.Result.CAPTURED)) {

            Order order = orderStore.get().findById(operation.getOrderId());
            //if (order.getStatus().equals(Order.Status.PAYMENT_PENDING)) {

            this.logger.debug("Updating order paid status");
            try {
                order.setStatus(Order.Status.PAID);
                orderStore.get().update(order);

                getObservationManager().notify(new OrderPaidEvent(), order, order.getOrderData());
            } catch (Exception e) {
                this.logger.error("Failed to update order status", e);
            }
            //}
        }
    }

    private ObservationManager getObservationManager()
    {
        if (this.observationManager == null) {
            try {
                this.observationManager = componentManager.getInstance(ObservationManager.class);
            } catch (ComponentLookupException e) {
                throw new RuntimeException("Failed to get an observation manager", e);
            }
        }

        return this.observationManager;
    }
}
