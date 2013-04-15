package org.mayocat.shop.payment;

/**
 * @version $Id$
 */
public class PaymentException extends Exception
{
    public PaymentException()
    {
        super();
    }

    public PaymentException(String message)
    {
        super(message);
    }

    public PaymentException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public PaymentException(Throwable t)
    {
        super(t);
    }
}
