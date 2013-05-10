package org.mayocat.accounts.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role
{

    GOD     ("god"),     /* Marketplace admin */
    ADMIN   ("admin"),   /* Shop admin */
    NONE    ("none")     /* No role */
    
    ;
    
    private String code;
    
    private Role(final String code)
    {
        this.code = code;
    };

    @JsonCreator
    public static Role fromJson(String text)
    {
        return valueOf(text.toUpperCase());
    }

    @Override
    public String toString() {
        return code;
    }
    
}
