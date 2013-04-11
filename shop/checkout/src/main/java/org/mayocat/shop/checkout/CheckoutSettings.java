package org.mayocat.shop.checkout;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class CheckoutSettings
{
    @Valid
    @JsonProperty
    private String defaultPaymentGateway;

    public String getDefaultPaymentGateway()
    {
        return defaultPaymentGateway;
    }
}
