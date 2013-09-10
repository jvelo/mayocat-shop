package org.mayocat.shop.billing;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.shop.payment.event.PaymentOperationEvent;
import org.mayocat.shop.payment.model.PaymentOperation;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.observation.EventListener;
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
            if (order.getStatus().equals(Order.Status.PAYMENT_PENDING)) {

                this.logger.debug("Updating order paid status");
                try {
                    order.setStatus(Order.Status.PAID);
                    orderStore.get().update(order);
                } catch (Exception e) {
                    this.logger.error("Failed to update order status", e);
                }
            }
        }
    }
}
