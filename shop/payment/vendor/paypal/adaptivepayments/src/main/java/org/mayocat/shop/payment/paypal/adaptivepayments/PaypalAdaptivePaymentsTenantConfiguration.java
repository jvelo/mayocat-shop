package org.mayocat.shop.payment.paypal.adaptivepayments;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @version $Id$
 */
public class PaypalAdaptivePaymentsTenantConfiguration
{
    @Pattern(regexp = "^(([^@\\s]+)@((?:[-a-zA-Z0-9]+\\.)+[a-zA-Z]{2,}))?$", message = "Not a valid email")
    @NotNull
    private String email;

    public String getEmail()
    {
        return email;
    }
}
