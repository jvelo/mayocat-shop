/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.internal;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.shop.payment.BasePaymentData;
import org.mayocat.shop.payment.ConfigurationException;
import org.mayocat.shop.payment.CreditCardPaymentData;
import org.mayocat.shop.payment.CreditCardPaymentGateway;
import org.mayocat.shop.payment.GatewayException;
import org.mayocat.shop.payment.GatewayFactory;
import org.mayocat.shop.payment.GatewayResponse;
import org.mayocat.shop.payment.PaymentData;
import org.mayocat.shop.payment.PaymentException;
import org.mayocat.shop.payment.PaymentGateway;
import org.mayocat.shop.payment.PaymentProcessor;
import org.mayocat.shop.payment.PaymentRequest;
import org.mayocat.shop.payment.PaymentStatus;
import org.mayocat.shop.payment.PaymentsSettings;
import org.mayocat.shop.payment.RequiredAction;
import org.mayocat.shop.payment.event.ExternalPaymentInitialized;
import org.mayocat.shop.payment.event.PaymentOperationEvent;
import org.mayocat.shop.payment.model.PaymentOperation;
import org.mayocat.shop.payment.store.PaymentOperationStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.observation.ObservationManager;

/**
 * @version $Id$
 */
@Component
public class DefaultPaymentProcessor implements PaymentProcessor
{
    @Inject
    private Map<String, GatewayFactory> gatewayFactories;

    @Inject
    private Provider<PaymentOperationStore> paymentOperationStore;

    @Inject
    private Provider<OrderStore> orderStore;

    @Inject
    private ObservationManager observationManager;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Logger logger;

    @Override
    public PaymentRequest requestPayment(Order order, Map<PaymentData, Object> data) throws PaymentException {

        PaymentsSettings paymentsSettings = configurationService.getSettings(PaymentsSettings.class);
        if (paymentsSettings.getGateways().getValue().size() <= 0) {
            throw new ConfigurationException("No payment gateway listed");
        }

        if (paymentsSettings.getGateways().getValue().size() > 1 && !data.containsKey(BasePaymentData.GATEWAY)) {
            // There are several gateways configured, and choice has not been made yet.
            return new PaymentRequest(PaymentStatus.NONE, RequiredAction.PRESENT_GATEWAY_CHOICE);
        }

        final String gatewayId = data.containsKey(BasePaymentData.GATEWAY)
                ? (String) data.get(BasePaymentData.GATEWAY)
                : paymentsSettings.getGateways().getValue().get(0);

        if (!gatewayFactories.containsKey(gatewayId)) {
            throw new PaymentException("Gateway id [" + gatewayId+ "] is not supported");
        }

        try {
            GatewayFactory factory = gatewayFactories.get(gatewayId);
            PaymentGateway gateway = factory.createGateway();

            if (gateway == null) {
                throw new PaymentException("Gateway with id [" + gatewayId+ "] could not be created.");
            }

            if (CreditCardPaymentGateway.class.isAssignableFrom(gateway.getClass())
                    && !dataContainsCreditCard(data)) {
                return new PaymentRequest(PaymentStatus.NONE, RequiredAction.INTERNAL_FORM,
                        ImmutableMap.<String, Object>of("creditCard", true));
            }

            PaymentRequestBuilder builder = new PaymentRequestBuilder();
            GatewayResponse gatewayResponse = gateway.purchase(order.getGrandTotal(), data);

            builder.withData(gatewayResponse.getData());

            if (gatewayResponse.isSuccessful()) {
                if (!Strings.isNullOrEmpty(gatewayResponse.getRedirectURL())) {
                    builder.withNextAction(RequiredAction.GET_EXTERNAL_URL);
                    builder.withRedirectionTarget(gatewayResponse.getRedirectURL());
                    observationManager.notify(new ExternalPaymentInitialized(), gatewayResponse.getData());
                } else if (!Strings.isNullOrEmpty(gatewayResponse.getFormURL())) {
                    builder.withNextAction(RequiredAction.POST_EXTERNAL_URL);
                    builder.withRedirectionTarget(gatewayResponse.getFormURL());
                    observationManager.notify(new ExternalPaymentInitialized(), gatewayResponse.getData());
                }

                if (gatewayResponse.getOperation().getResult().equals(PaymentOperation.Result.CAPTURED)) {
                    builder.withStatus(PaymentStatus.CAPTURED);
                    order.setStatus(Order.Status.PAID);
                } else if (gatewayResponse.getOperation().getResult().equals(PaymentOperation.Result.REFUSED)){
                    builder.withStatus(PaymentStatus.REFUSED);
                    if (CreditCardPaymentGateway.class.isAssignableFrom(gateway.getClass())) {
                        builder.withNextAction(RequiredAction.INTERNAL_FORM);
                        builder.withData(ImmutableMap.<String, Object> builder()
                                .putAll(gatewayResponse.getData())
                                .put("creditCard", true)
                                .build());
                    }

                } else {
                    builder.withStatus(PaymentStatus.INITIALIZED);
                    order.setStatus(Order.Status.PAYMENT_PENDING);
                }
                try {
                    orderStore.get().update(order);
                    PaymentOperation operation = gatewayResponse.getOperation();
                    operation.setOrderId(order.getId());
                    paymentOperationStore.get().create(operation);

                    observationManager.notify(new PaymentOperationEvent(), gatewayResponse.getOperation());
                } catch (EntityDoesNotExistException | InvalidEntityException | EntityAlreadyExistsException e) {
                    this.logger.error("Order error while processing payment", e);
                    throw new PaymentException(e);
                }
            } else {
                throw new PaymentException("Error while communicating with gateway");
            }
            return builder.build();

        } catch (GatewayException e) {
            this.logger.error("Payment error while checking out cart", e);
            throw new PaymentException(e);
        }
    }

    private static boolean dataContainsCreditCard(Map<PaymentData, Object> data) {
        return data.containsKey(CreditCardPaymentData.CARD_NUMBER)
                && !Strings.isNullOrEmpty((String) data.get(CreditCardPaymentData.CARD_NUMBER));
    }
}
