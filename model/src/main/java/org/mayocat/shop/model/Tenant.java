package org.mayocat.shop.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.base.Objects;

public class Tenant implements HandleableEntity
{    
    @JsonIgnore
    Long id;

    @NotNull
    @Pattern (message="Only word characters or hyphens", regexp="\\w[\\w-]*\\w")
    String handle;
    
    List<String> aliases;

    ///////////////////////////////////////////////////
    
    public Tenant()
    {
    }
    
    public Tenant(String handle)
    {
        setHandle(handle);
    }
    
    ///////////////////////////////////////////////////
    
    public Long getId()
    {
        return id;
    }
    
    public String getHandle()
    {
        return handle;
    }

    public void setHandle(String handle)
    {
        this.handle = handle;
    }

    public List<String> getAliases()
    {
        return aliases;
    }

    public void setAliases(List<String> aliases)
    {
        this.aliases = aliases;
    }

    ///////////////////////////////////////////////////
    
    public void fromTenant(Tenant t)
    {
        this.setHandle(t.getHandle());        
        this.setAliases(t.getAliases());
    }
    
    // ///////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tenant other = (Tenant) obj;

        return Objects.equal(this.handle, other.handle);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.handle);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).addValue(this.handle).toString();
    }

}
