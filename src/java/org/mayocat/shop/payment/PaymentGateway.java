package org.mayocat.shop.payment;

import java.util.Map;

import org.mayocat.shop.grails.Order;
import org.mayocat.shop.grails.OrderStatus;

/**
 * A possible method for paying orders in MayocatShop.
 * 
 * @version $Id$
 */
public interface PaymentGateway
{

    /**
     * Validates the passed payment gateway configuration is correct and will let the payment gateway behave correctly.
     * For example, a "payment by check" payment gateway will verify its configuration has the proper "Order to" field
     * and a proper "postal address" ; so that customers have all information needed to send their check. A tiers
     * payment system will require a API key.
     * 
     * @param gatewayConfiguration the configuration map to verify
     * @return a map of errors. Empty map or null means no-error, thus a valid configuration. Error keys correspond
     *         either to a configuration field, or to special keys for cross-field errors. Error values are
     *         human-readable error messages.
     */
    Map<String, String> validateConfiguration(final Map<String, Object> gatewayConfiguration);

    Map<String, Object> prepareBeforePayment(final Order order, final Map<String, Object> gatewayConfiguration);

    /**
     * @param parameters
     * @param gatewayConfiguration
     * @return
     */
    PaymentResponse acknowledgePayment(final Map<String, Object> responseParameters,
        final Map<String, Object> gatewayConfiguration);

    boolean hasExternalForm();

}
