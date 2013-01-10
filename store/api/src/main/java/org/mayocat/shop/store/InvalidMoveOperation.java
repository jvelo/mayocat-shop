package org.mayocat.shop.store;

public class InvalidMoveOperation extends Exception
{

    /**
     * Generated serial UID. Change when the serialization of this class changes.
     */
    private static final long serialVersionUID = 1895587120925895087L;

    public InvalidMoveOperation()
    {
        super();
    }

    public InvalidMoveOperation(String message)
    {
        super(message);
    }
}
