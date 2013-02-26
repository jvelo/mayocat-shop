package org.mayocat.store;

import com.google.common.collect.ImmutableList;

public class InvalidEntityException extends Exception
{
    /**
     * Generated serial UID. Change when the serialization form of this exception changes.
     */
    private static final long serialVersionUID = 645796421122785119L;
    
    private final ImmutableList<String> errors;
        
    public InvalidEntityException(String message, Iterable<String> errors)
    {
        super(message);
        this.errors = ImmutableList.copyOf(errors);
    }

    public ImmutableList<String> getErrors() {
        return errors;
    }
}
