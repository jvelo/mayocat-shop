package org.mayocat.shop.payment;

import java.util.Map;

/**
 * @version $Id$
 */
public final class PaymentResponse
{
    private Map<String, Object> data;

    private boolean isSuccessful;

    public PaymentResponse(boolean isSuccessful, Map<String, Object> data)
    {
        this.isSuccessful = isSuccessful;
        this.data = data;
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public boolean isSuccessful()
    {
        return isSuccessful;
    }
}
