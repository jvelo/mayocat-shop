package org.mayocat.shop.payment;

/**
 * @version $Id$
 */
public class GatewayException extends Exception
{
    public GatewayException()
    {
        super();
    }

    public GatewayException(String message)
    {
        super(message);
    }

    public GatewayException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GatewayException(Throwable t)
    {
        super(t);
    }
}
