package org.mayocat.store;

public class EntityAlreadyExistsException extends Exception
{
    /**
     * Change when the serialization form of this exception changes.
     */
    private static final long serialVersionUID = -153504107891229925L;

    public EntityAlreadyExistsException()
    {
        super();
    }

    public EntityAlreadyExistsException(String message)
    {
        super(message);
    }

}
