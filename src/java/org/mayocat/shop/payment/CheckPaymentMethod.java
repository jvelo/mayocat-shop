package org.mayocat.shop.payment;

import java.util.HashMap;
import java.util.Map;

import org.mayocat.shop.grails.Order;

import com.google.common.base.Strings;

public class CheckPaymentMethod implements PaymentMethod
{

    /**
     * {@inheritDoc}
     * 
     * @see org.mayocat.shop.payment.PaymentMethod#validateConfiguration(java.util.Map)
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

    public boolean hasPrepareStep()
    {
        return false;
    }

    public String displayPrepareStep()
    {
        return null;
    }

    public void preparePayment(final Order order)
    {
        // Nothing
    }

    public void executePayment(final Order order)
    {
    }

    public String displayExecuteStep()
    {
        return null;
    }

}
