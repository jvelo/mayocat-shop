package org.mayocat.shop.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.base.Objects;

public class Tenant implements EntityWithSlug
{    
    @JsonIgnore
    Long id;

    @NotNull
    @Pattern (message="Only word characters or hyphens", regexp="\\w[\\w-]*\\w")
    String slug;
    
    List<String> aliases;
    
    Shop shop;

    ///////////////////////////////////////////////////
    
    public Tenant()
    {
    }
    
    public Tenant(String slug)
    {
        setSlug(slug);
    }
    
    ///////////////////////////////////////////////////
    
    public Long getId()
    {
        return id;
    }
    
    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public List<String> getAliases()
    {
        return aliases;
    }

    public void setAliases(List<String> aliases)
    {
        this.aliases = aliases;
    }

    public Shop getShop()
    {
        return shop;
    }
    
    public void setShop(Shop shop)
    {
        this.shop = shop;
    }
    
    ///////////////////////////////////////////////////
    
    public void fromTenant(Tenant t)
    {
        this.setSlug(t.getSlug());        
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

        return Objects.equal(this.slug, other.slug);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.slug);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).addValue(this.slug).toString();
    }

}
