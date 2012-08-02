package org.mayocat.shop.model;

import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@PersistenceCapable(table = "tenant", detachable = "true")
@Extension(vendorName="datanucleus", key="multitenancy-disable", value="true")
public class Tenant extends Entity
{
    public Tenant(@Valid String handle)
    {
        setHandle(handle);
    }
    
    @Index
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Index
    @NotNull
    //@Unique
    @Pattern (message="Only word characters or hyphens", regexp="\\w[\\w-]*\\w")
    private String handle;

    //, defaultFetchGroup="true"
    //@Persistent(mappedBy="tenant")
    //@Element(dependent = "true")
    //List<Product> products;
    
    @Element(dependent="true", column="alias", types=String.class)
    List<String> aliases;

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

    /*
    public void addToProducts(Product p)
    {
        if (this.products == null) {
            this.products = new ArrayList<Product>();
        }
        this.products.add(p);
    }
    */
    
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

        return com.google.common.base.Objects.equal(this.handle, other.handle);
    }

    @Override
    public int hashCode()
    {
        return com.google.common.base.Objects.hashCode(this.handle);
    }

    @Override
    public String toString()
    {
        return com.google.common.base.Objects.toStringHelper(this)
            .addValue(this.handle).toString();
    }

}
