package org.mayocat.shop.checkout;

/**
 * @version $Id$
 */
public class CheckoutException extends Exception
{
    public CheckoutException()
    {
    }

    public CheckoutException(String message)
    {
        super(message);
    }

    public CheckoutException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CheckoutException(Throwable cause)
    {
        super(cause);
    }
}
