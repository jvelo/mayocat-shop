package org.mayocat.shop.store;

public class EntityDoesNotExistsException extends Exception
{
    /**
     * Change when the serialization form of this exception changes.
     */
    private static final long serialVersionUID = 2219941249999412472L;

    public EntityDoesNotExistsException()
    {
        super();
    }
    
    public EntityDoesNotExistsException(String message)
    {
        super(message);
    }

}
