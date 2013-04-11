package org.mayocat.shop.checkout.internal;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.checkout.CheckoutException;
import org.mayocat.shop.checkout.CheckoutRegister;
import org.mayocat.shop.checkout.CheckoutResponse;
import org.mayocat.shop.checkout.CheckoutSettings;
import org.mayocat.shop.checkout.CustomerDetails;
import org.mayocat.shop.payment.BaseOption;
import org.mayocat.shop.payment.GatewayFactory;
import org.mayocat.shop.payment.Option;
import org.mayocat.shop.payment.PaymentException;
import org.mayocat.shop.payment.PaymentGateway;
import org.mayocat.shop.payment.PaymentResponse;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component
public class DefaultCheckoutRegister implements CheckoutRegister
{
    @Inject
    private Logger logger;

    @Inject
    private CheckoutSettings checkoutSettings;

    @Inject
    private Map<String, GatewayFactory> gatewayFactories;

    @Override
    public CheckoutResponse checkout(Cart cart, UriInfo uriInfo, CustomerDetails customerDetails) throws CheckoutException
    {
        String defaultGatewayFactory = checkoutSettings.getDefaultPaymentGateway();

        // Right now only the default gateway factory is supported.
        // In the future individual tenants will be able to setup their own payment gateway.

        if (!gatewayFactories.containsKey(defaultGatewayFactory)) {
            throw new CheckoutException("No gateway factory is available to handle the checkout.");
        }

        GatewayFactory factory = gatewayFactories.get(defaultGatewayFactory);

        PaymentGateway gateway = factory.createGateway();

        Map<Option, Object> options = Maps.newHashMap();
        options.put(BaseOption.CANCEL_URL, uriInfo.getBaseUri() + "checkout/payment/cancel");
        options.put(BaseOption.RETURN_URL, uriInfo.getBaseUri() + "checkout/payment/return");
        options.put(BaseOption.CURRENCY, cart.getCurrency());

        try {

            CheckoutResponse response = new CheckoutResponse();
            PaymentResponse paymentResponse = gateway.purchase(cart.getTotal(), options);

            if (paymentResponse.isSuccessful()) {
                // OK
                if (paymentResponse.isRedirect()) {
                    response.setRedirectURL(Optional.fromNullable(paymentResponse.getRedirectURL()));
                }

                return response;
            }
            else {
                throw new CheckoutException("Payment was not successful");
            }

        } catch (PaymentException e) {
            this.logger.error("Payment error while checking out cart", e);
            throw new CheckoutException(e);
        }

    }

    @Override
    public boolean requiresForm()
    {
        return false;
    }

}
