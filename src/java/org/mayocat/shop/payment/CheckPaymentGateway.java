package org.mayocat.shop.payment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.mayocat.shop.grails.Order;
import org.mayocat.shop.grails.OrderStatus;

import com.google.common.base.Strings;

public class CheckPaymentGateway implements PaymentGateway
{

    /**
     * {@inheritDoc}
     * 
     * @see org.mayocat.shop.payment.PaymentGateway#validateConfiguration(java.util.Map)
     */
    public Map<String, String> validateConfiguration(Map<String, Object> configuration)
    {
        Map<String, String> errors = new HashMap<String, String>();
        if (configuration == null) {
            errors.put("_null", "Configuration cannot be null");
        } else {
            if (Strings.isNullOrEmpty((String) configuration.get("orderto"))) {
                errors.put("orderto", "Order to cannot be empty !");
            }
            if (Strings.isNullOrEmpty((String) configuration.get("sendto"))) {
                errors.put("sendto", "Send to cannot be empty !");
            }
        }
        return errors;
    }

    @Override
    public Map<String, Object> prepareBeforePayment(Order order, Map<String, Object> configuration)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasExternalForm()
    {
        return false;
    }

    @Override
    public PaymentResponse acknowledgePayment(Map<String, Object> parameters, Map<String, Object> configuration)
    {
        Long orderId = (Long) parameters.get("orderId");
        PaymentResponse response =
            new PaymentResponse(orderId, OrderStatus.WAITING_FOR_PAYMENT, Collections.<String, Object> emptyMap(),
                null, null);
        return response;

    }

}
