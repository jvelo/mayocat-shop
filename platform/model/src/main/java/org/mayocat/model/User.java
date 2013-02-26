package org.mayocat.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.mayocat.jackson.PasswordSerializer;
import org.mayocat.model.annotation.SearchIndex;
import org.mayocat.model.reference.EntityReference;

import com.google.common.base.Optional;

public class User implements Entity
{
    @JsonIgnore
    Long id;

    @SearchIndex
    @NotNull
    @Size(min = 1)
    String slug;

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

    public User()
    {
    }
    
    public User(Long id)
    {
        this.id = id;
    }
    
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

    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getSlug()
    {
        return this.slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    @Override
    public EntityReference getReference()
    {
        return new EntityReference("user", getSlug(), Optional.<EntityReference>absent());
    }

}
