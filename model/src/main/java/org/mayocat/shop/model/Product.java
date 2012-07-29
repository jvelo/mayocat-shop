package org.mayocat.shop.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@PersistenceCapable(
    table = "product",
    detachable="true"
)
@Uniques({@Unique(name="UNIQUE_HANDLE_PER_TENANT", members={"handle", "tenant"})})
public class Product
{
    @Index
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Index
    @NotNull
    @Size(min = 1)
    private String handle;
    
    private String tenant;
    
    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }
    
    public String getHandle()
    {
        return handle;
    }

    public void setHandle(String handle)
    {
        this.handle = handle;
    }
    
    ////////////////////////////////////////////////

    @Override  
    public boolean equals(Object obj)  
    {  
       if (obj == null)  
       {  
          return false;  
       }  
       if (getClass() != obj.getClass())  
       {  
          return false;  
       }  
       final Product other = (Product) obj;  
         
       return   com.google.common.base.Objects.equal(this.id, other.id)  
             && com.google.common.base.Objects.equal(this.tenant, other.tenant)  
             && com.google.common.base.Objects.equal(this.handle, other.handle);  
    }  
    
    @Override
    public int hashCode()  
    {  
        return com.google.common.base.Objects.hashCode(this.handle, this.tenant, this.id);  
    }
    
    @Override  
    public String toString()  
    {  
       return com.google.common.base.Objects.toStringHelper(this)  
                 .addValue(this.id)  
                 .addValue(this.tenant)  
                 .addValue(this.handle)    
                 .toString();  
    }  
    
}
