package org.mayocat.shop.model;

import javax.validation.constraints.NotNull;

public class User extends Entity
{
    Long id;

    String email;

    /**
     * The password hash.
     */
    @NotNull
    String password;

    ///////////////////////////////////////////////////
    
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Long getId()
    {
        return id;
    }
    
    
    
}
