package org.mayocat.store;

public class EntityDoesNotExistException extends Exception
{
    /**
     * Change when the serialization form of this exception changes.
     */
    private static final long serialVersionUID = 2219941249999412472L;

    public EntityDoesNotExistException()
    {
        super();
    }
    
    public EntityDoesNotExistException(String message)
    {
        super(message);
    }

}
