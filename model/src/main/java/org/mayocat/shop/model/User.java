package org.mayocat.shop.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.mayocat.shop.jackson.PasswordSerializer;

public class User extends Entity
{
    @JsonIgnore
    Long id;

    @Pattern(regexp = "^(([^@\\s]+)@((?:[-a-zA-Z0-9]+\\.)+[a-zA-Z]{2,}))?$", message = "Not a valid email")
    @NotNull
    String email;

    /**
     * The password hash.
     */
    @NotNull
    @JsonSerialize(using = PasswordSerializer.class)
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
