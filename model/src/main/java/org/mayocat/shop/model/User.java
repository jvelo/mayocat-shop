package org.mayocat.shop.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.mayocat.shop.jackson.PasswordSerializer;
import org.mayocat.shop.model.annotation.SearchIndex;

public class User implements HandleableEntity
{
    @JsonIgnore
    Long id;

    @SearchIndex
    @NotNull
    @Size(min = 1)
    String handle;

    @SearchIndex
    @Pattern(regexp = "^(([^@\\s]+)@((?:[-a-zA-Z0-9]+\\.)+[a-zA-Z]{2,}))?$", message = "Not a valid email")
    @NotNull
    String email;

    /**
     * The password hash.
     */
    @NotNull
    @JsonSerialize(using = PasswordSerializer.class)
    String password;

    // /////////////////////////////////////////////////

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

    public String getHandle()
    {
        return this.handle;
    }

    public void setHandle(String handle)
    {
        this.handle = handle;
    }

}
