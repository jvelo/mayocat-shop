package org.mayocat.shop.authorization.cookies;

public class EncryptionException extends Exception
{

    /**
     * Generated serial version. Change when the serialization of this class changes.
     */
    private static final long serialVersionUID = -7902103228849842206L;

    public EncryptionException()
    {
        super();
    }
    
    public EncryptionException(Throwable t)
    {
        super(t);
    }
    
    public EncryptionException(String message)
    {
        super(message);
    }

    public EncryptionException(String message, Throwable t)
    {
        super(message, t);
    }
}
