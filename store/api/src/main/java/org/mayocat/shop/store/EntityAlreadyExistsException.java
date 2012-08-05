package org.mayocat.shop.store;

public class EntityAlreadyExistsException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = -153504107891229925L;

    public EntityAlreadyExistsException(){
        super();
    }
    
    public EntityAlreadyExistsException(String message){
        super(message);
    }
    
    public EntityAlreadyExistsException(Throwable cause){
        super(cause);
    }
    
    public EntityAlreadyExistsException(String message, Throwable cause){
        super(message, cause);
    }
}
