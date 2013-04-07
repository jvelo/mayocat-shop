package org.mayocat.shop.payment.paypal.adaptivepayments;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mayocat.shop.payment.PaymentException;
import org.mayocat.shop.payment.PaymentGateway;
import org.mayocat.shop.payment.PaymentResponse;

import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
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
    private InputStream configInputStream;

    public PaypalAdaptivePaymentsPaymentGateway(InputStream configInputStream)
    {
        this.configInputStream = configInputStream;
    }

    @Override
    public PaymentResponse purchase(BigDecimal amount, Map<String, Object> options) throws PaymentException
    {
        PayRequest request = new PayRequest();

        RequestEnvelope requestEnvelope = new RequestEnvelope("en_US"); // locale -> errorLanguage
        List<Receiver> receivers = new ArrayList<Receiver>();
        Receiver rec = new Receiver();
        rec.setAmount(amount.doubleValue());
        rec.setEmail("jerome@velociter.fr");
        ReceiverList receiverList = new ReceiverList(receivers);
        request.setReceiverList(receiverList);
        request.setRequestEnvelope(requestEnvelope);
        ClientDetailsType clientDetails = new ClientDetailsType();
        request.setClientDetails(clientDetails);

        try {
            AdaptivePaymentsService service = new AdaptivePaymentsService(this.configInputStream);

            PayResponse response = service.pay(request);
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            if (response.getResponseEnvelope().getAck().toString().equalsIgnoreCase("SUCCESS")) {
                // success
                map.put("ack", response.getResponseEnvelope().getAck());
                map.put("correlationId", response.getResponseEnvelope().getCorrelationId());
                map.put("timestamp", response.getResponseEnvelope().getTimestamp());
                map.put("payKey", response.getPayKey());
                map.put("paymentExecutionStatus", response.getPaymentExecStatus());
                if (response.getDefaultFundingPlan() != null) {
                    map.put("defaultFundingPlan", response.getDefaultFundingPlan().getFundingPlanId());
                }
                map.put("redirectUrl", "href=https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_ap-payment&paykey=" +
                        response.getPayKey());
                return new PaymentResponse(true, map);
            } else {
                // failure
                map.put("error", response.getError());
                return new PaymentResponse(false, map);
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
}

