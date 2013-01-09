package org.mayocat.shop.store;

public class StoreException extends RuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = -153504107891229925L;

    public StoreException()  {
        super();
    }
    
    public StoreException(String message){
        super(message);
    }
    
    public StoreException(Throwable cause){
        super(cause);
    }
    
    public StoreException(String message, Throwable cause){
        super(message, cause);
    }
}
