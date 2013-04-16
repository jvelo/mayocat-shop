package org.mayocat.shop.payment;

import java.util.Map;

import org.mayocat.shop.payment.model.PaymentOperation;

/**
 * @version $Id$
 */
public final class PaymentResponse
{

    private boolean isSuccessful;

    private String redirectURL;

    private boolean isRedirect = false;

    private PaymentOperation operation;

    public PaymentResponse(boolean isSuccessful, PaymentOperation operation)
    {
        this.isSuccessful = isSuccessful;
        this.operation = operation;
    }

    public boolean isSuccessful()
    {
        return isSuccessful;
    }

    public String getRedirectURL()
    {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL)
    {
        this.redirectURL = redirectURL;
    }

    public boolean isRedirect()
    {
        return isRedirect;
    }

    public void setRedirect(boolean redirect)
    {
        isRedirect = redirect;
    }

    public PaymentOperation getOperation()
    {
        return operation;
    }

    public void setOperation(PaymentOperation operation)
    {
        this.operation = operation;
    }
}
