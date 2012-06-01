package org.mayocat.shop.payment;

import java.util.Map;

import org.mayocat.shop.grails.Order;

/**
 * A possible method for paying orders in MayocatShop.
 * 
 * @version $Id$
 */
public interface PaymentMethod
{

    /**
     * Validates the passed payment gateway configuration is correct and will let the payment gateway behave correctly.
     * For example, a "payment by check" payment gateway will verify its configuration has the proper "Order to" field
     * and a proper "postal address" ; so that customers have all information needed to send their check. A tiers
     * payment system will require a API key.
     * 
     * @param configuration the configuration map to verify
     * @return a map of errors. Empty map or null means no-error, thus a valid configuration. Error keys correspond
     *         either to a configuration field, or to special keys for cross-field errors. Error values are
     *         human-readable error messages.
     */
    Map<String, String> validateConfiguration(Map<String, Object> configuration);

    boolean hasPrepareStep();

    String displayPrepareStep();

    void preparePayment(final Order order);

    String displayExecuteStep();

    void executePayment(final Order order);

}