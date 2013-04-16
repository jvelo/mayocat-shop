package org.mayocat.shop.payment.paypal.adaptivepayments;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mayocat.shop.payment.BaseOption;
import org.mayocat.shop.payment.Option;
import org.mayocat.shop.payment.PaymentException;
import org.mayocat.shop.payment.PaymentGateway;
import org.mayocat.shop.payment.PaymentResponse;
import org.mayocat.shop.payment.api.resources.PaymentResource;
import org.mayocat.shop.payment.model.PaymentOperation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.ipn.IPNMessage;
import com.paypal.sdk.exceptions.OAuthException;
import com.paypal.svcs.services.AdaptivePaymentsService;
import com.paypal.svcs.types.ap.PayRequest;
import com.paypal.svcs.types.ap.PayResponse;
import com.paypal.svcs.types.ap.Receiver;
import com.paypal.svcs.types.ap.ReceiverList;
import com.paypal.svcs.types.common.ClientDetailsType;
import com.paypal.svcs.types.common.RequestEnvelope;

/**
 * @version $Id$
 */
public class PaypalAdaptivePaymentsPaymentGateway implements PaymentGateway
{
    public static final String ACTION_TYPE_PAY = "PAY";

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
    public boolean isExternal()
    {
        return true;
    }

    @Override
    public PaymentResponse purchase(BigDecimal amount, Map<Option, Object> options) throws PaymentException
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
        request.setReturnUrl((String) options.get(BaseOption.RETURN_URL));
        request.setCancelUrl((String) options.get(BaseOption.CANCEL_URL));
        request.setActionType(ACTION_TYPE_PAY);
        request.setCurrencyCode(((Currency) options.get(BaseOption.CURRENCY)).getCurrencyCode());

        String baseURI = (String) options.get(BaseOption.BASE_URL);
        String orderId = ((Long) options.get(BaseOption.ORDER_ID)).toString();

        // -> Set the tracking ID when we have moved entities ID to UUID
        // request.setTrackingId(orderId);

        request.setIpnNotificationUrl(
                baseURI + PaymentResource.PATH + "/" + orderId + "/" + PaymentResource.ACKNOWLEDGEMENT_PATH + "/" +
                        PaypalAdaptivePaymentsGatewayFactory.ID);

        try {
            AdaptivePaymentsService service = new AdaptivePaymentsService(this.configInputStream);

            PayResponse response = service.pay(request);
            PaymentOperation operation = new PaymentOperation();
            operation.setGatewayId(PaypalAdaptivePaymentsGatewayFactory.ID);
            operation.setExternalId((String) response.getPayKey());

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

                PaymentResponse res;
                operation.setMemo(map);

                if (response.getPaymentExecStatus().equals("CREATED")) {
                    res = new PaymentResponse(true, operation);
                    operation.setResult(PaymentOperation.Result.INITIALIZED);
                    // This means the payment needs approval on paypal site
                    res.setRedirectURL("https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_ap-payment&paykey=" +
                            response.getPayKey());
                    res.setRedirect(true);
                } else if (response.getPaymentExecStatus().equals("COMPLETED")) {
                    operation.setResult(PaymentOperation.Result.CAPTURED);
                    res = new PaymentResponse(true, operation);
                    // This means the payment has been approved and the funds transferred
                } else {
                    throw new PaymentException("Unknown payment execution status");
                }

                return res;
            } else {
                // failure
                operation.setResult(PaymentOperation.Result.FAILED);
                operation.setMemo(map);
                map.put("error", response.getError());
                return new PaymentResponse(false, operation);
            }
        } catch (IOException e) {
            throw new PaymentException(e);
        } catch (MissingCredentialException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClientActionRequiredException e) {
            e.printStackTrace();
        } catch (SSLConfigurationException e) {
            e.printStackTrace();
        } catch (OAuthException e) {
            e.printStackTrace();
        } catch (HttpErrorException e) {
            e.printStackTrace();
        } catch (InvalidCredentialException e) {
            e.printStackTrace();
        } catch (InvalidResponseDataException e) {
            e.printStackTrace();
        }
        throw new PaymentException();
    }

    public PaymentResponse acknowledge(Map<String, List<String>> data) throws PaymentException
    {

        IPNMessage ipnListener = new IPNMessage(convertDataMap(data));
        boolean isIpnVerified = ipnListener.validate();

        Map map = ipnListener.getIpnMap();

        PaymentOperation operation = new PaymentOperation();
        operation.setGatewayId(PaypalAdaptivePaymentsGatewayFactory.ID);
        operation.setExternalId((String) map.get("pay_key"));
        operation.setMemo(map);

        String status = (String) map.get("status");
        PaymentResponse response;

        if (isIpnVerified && status.equalsIgnoreCase("Completed")) {
            operation.setResult(PaymentOperation.Result.CAPTURED);
            response = new PaymentResponse(true, operation);
        } else {
            operation.setResult(PaymentOperation.Result.FAILED);
            response = new PaymentResponse(false, operation);
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

