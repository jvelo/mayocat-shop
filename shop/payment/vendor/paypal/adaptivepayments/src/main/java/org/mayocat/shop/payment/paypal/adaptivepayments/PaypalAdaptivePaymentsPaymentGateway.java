/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.paypal.adaptivepayments;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mayocat.shop.payment.BasePaymentData;
import org.mayocat.shop.payment.GatewayException;
import org.mayocat.shop.payment.GatewayResponse;
import org.mayocat.shop.payment.PaymentData;
import org.mayocat.shop.payment.PaymentGateway;
import org.mayocat.shop.payment.api.resources.PaymentResource;
import org.mayocat.shop.payment.model.PaymentOperation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.paypal.core.ConfigManager;
import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.ipn.IPNMessage;
import com.paypal.sdk.exceptions.OAuthException;
import com.paypal.svcs.services.AdaptivePaymentsService;
import com.paypal.svcs.types.ap.DisplayOptions;
import com.paypal.svcs.types.ap.PayRequest;
import com.paypal.svcs.types.ap.PayResponse;
import com.paypal.svcs.types.ap.Receiver;
import com.paypal.svcs.types.ap.ReceiverList;
import com.paypal.svcs.types.ap.SetPaymentOptionsRequest;
import com.paypal.svcs.types.ap.SetPaymentOptionsResponse;
import com.paypal.svcs.types.common.ClientDetailsType;
import com.paypal.svcs.types.common.RequestEnvelope;

/**
 * @version $Id$
 */
public class PaypalAdaptivePaymentsPaymentGateway implements PaymentGateway
{
    public static final String ACTION_TYPE_CREATE = "CREATE";

    public static final String ACTION_TYPE_PAY = "PAY";

    public static final String EXECUTION_STATUS_CREATED = "CREATED";

    public static final String EXECUTION_STATUS_COMPLETED = "COMPLETED";

    private InputStream configInputStream;

    private String receiverEmail;

    public PaypalAdaptivePaymentsPaymentGateway(InputStream configInputStream, String receiverEmail)
    {
        Preconditions.checkNotNull(configInputStream);
        Preconditions.checkNotNull(receiverEmail);

        this.configInputStream = configInputStream;
        this.receiverEmail = receiverEmail;
    }

    @Override
    public GatewayResponse purchase(BigDecimal amount, Map<PaymentData, Object> options) throws GatewayException
    {
        PayRequest request = new PayRequest();

        RequestEnvelope requestEnvelope = new RequestEnvelope("en_US"); // locale -> errorLanguage
        List<Receiver> receivers = new ArrayList<Receiver>();
        Receiver receiver = new Receiver();
        receiver.setAmount(amount.doubleValue());
        receiver.setEmail(receiverEmail);
        receivers.add(receiver);
        ReceiverList receiverList = new ReceiverList(receivers);
        request.setReceiverList(receiverList);
        request.setRequestEnvelope(requestEnvelope);
        ClientDetailsType clientDetails = new ClientDetailsType();
        request.setClientDetails(clientDetails);
        request.setReturnUrl((String) options.get(BasePaymentData.RETURN_URL));
        request.setCancelUrl((String) options.get(BasePaymentData.CANCEL_URL));
        request.setActionType(ACTION_TYPE_CREATE);
        request.setCurrencyCode(((Currency) options.get(BasePaymentData.CURRENCY)).getCurrencyCode());

        String baseURI = (String) options.get(BasePaymentData.BASE_URL);
        String orderId = options.get(BasePaymentData.ORDER_ID).toString();

        // -> FIXME determine if we want to set the order ID as tracking ID.
        // We need to know for sure it can only be used once
        //request.setTrackingId(orderId);

        request.setIpnNotificationUrl(
                baseURI + PaymentResource.PATH + "/" + orderId + "/" + PaymentResource.ACKNOWLEDGEMENT_PATH + "/" +
                        PaypalAdaptivePaymentsGatewayFactory.ID);

        try {
            AdaptivePaymentsService service = new AdaptivePaymentsService(this.configInputStream);
            PayResponse response = service.pay(request);
            PaymentOperation operation = new PaymentOperation();
            operation.setGatewayId(PaypalAdaptivePaymentsGatewayFactory.ID);
            operation.setExternalId(response.getPayKey());

            Map<String, Object> map = new LinkedHashMap<String, Object>();
            if (response.getResponseEnvelope().getAck().toString().equalsIgnoreCase("SUCCESS")) {
                // call success

                map.put("ack", response.getResponseEnvelope().getAck());
                map.put("correlationId", response.getResponseEnvelope().getCorrelationId());
                map.put("timestamp", response.getResponseEnvelope().getTimestamp());
                map.put("payKey", response.getPayKey());
                map.put("paymentExecutionStatus", response.getPaymentExecStatus());
                if (response.getDefaultFundingPlan() != null) {
                    map.put("defaultFundingPlan", response.getDefaultFundingPlan().getFundingPlanId());
                }

                GatewayResponse gatewayResponse;
                operation.setMemo(map);

                if (response.getPaymentExecStatus().equals(EXECUTION_STATUS_CREATED)) {

                    gatewayResponse = new GatewayResponse(true, operation);
                    operation.setResult(PaymentOperation.Result.INITIALIZED);

                    SetPaymentOptionsRequest optionsRequest = new SetPaymentOptionsRequest();
                    optionsRequest.setPayKey(response.getPayKey());
                    DisplayOptions displayOptions = new DisplayOptions();
                    displayOptions.setBusinessName(receiverEmail);
                    optionsRequest.setDisplayOptions(displayOptions);
                    RequestEnvelope envelope = new RequestEnvelope("en_US");
                    optionsRequest.setRequestEnvelope(requestEnvelope);
                    SetPaymentOptionsResponse resp = service.setPaymentOptions(optionsRequest);
                    if (resp != null) {
                        if (resp.getResponseEnvelope().getAck().toString().equalsIgnoreCase("SUCCESS")) {
                            // This means the payment needs approval on paypal site
                            String redirectURL = ConfigManager.getInstance().getValueWithDefault("service.RedirectURL",
                                    "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=");
                            gatewayResponse.setRedirectURL(redirectURL + "_ap-payment&paykey=" + response.getPayKey());
                        } else {
                            throw new GatewayException("Failed to send payment options : " + resp.getError());
                        }
                    } else {
                        throw new GatewayException("Failed to send payment options");
                    }
                } else if (response.getPaymentExecStatus().equals(EXECUTION_STATUS_COMPLETED)) {
                    operation.setResult(PaymentOperation.Result.CAPTURED);
                    gatewayResponse = new GatewayResponse(true, operation);
                    // This means the payment has been approved and the funds transferred
                } else {
                    throw new GatewayException("Unknown payment execution status");
                }

                return gatewayResponse;
            } else {
                // failure
                map.put("error", response.getError());
                operation.setResult(PaymentOperation.Result.FAILED);
                operation.setMemo(map);
                return new GatewayResponse(false, operation);
            }
        } catch (Exception e) {
            throw new GatewayException(e);
        }
    }

    public GatewayResponse acknowledge(UUID orderId, Map<String, List<String>> data) throws GatewayException
    {

        IPNMessage ipnListener = new IPNMessage(convertDataMap(data));
        boolean isIpnVerified = ipnListener.validate();

        Map map = ipnListener.getIpnMap();

        PaymentOperation operation = new PaymentOperation();
        operation.setGatewayId(PaypalAdaptivePaymentsGatewayFactory.ID);
        operation.setExternalId((String) map.get("pay_key"));
        operation.setMemo(map);

        String status = (String) map.get("status");
        GatewayResponse response;

        if (isIpnVerified && status.equalsIgnoreCase("Completed")) {
            operation.setResult(PaymentOperation.Result.CAPTURED);
            response = new GatewayResponse(true, operation);
        } else {
            operation.setResult(PaymentOperation.Result.FAILED);
            response = new GatewayResponse(false, operation);
        }

        return response;
    }

    private Map<String, String[]> convertDataMap(Map<String, List<String>> data)
    {
        Map<String, String[]> converted = Maps.newHashMap();
        for (String key : data.keySet()) {
            String[] value = data.get(key).toArray(new String[data.get(key).size()]);
            converted.put(key, value);
        }
        return converted;
    }
}

