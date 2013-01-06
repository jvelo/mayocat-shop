package org.mayocat.shop.model;

public enum Role
{

    GOD     ("god"),     /* Marketplace admin */
    ADMIN   ("admin")    /* Shop admin */
    
    ;
    
    private String code;
    
    private Role(final String code)
    {
        this.code = code;
    };
    
    @Override
    public String toString() {
        return code;
    }
    
}
