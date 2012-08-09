package org.mayocat.shop.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.mayocat.shop.model.annotation.SearchIndex;

public class Product extends Entity
{
    @JsonIgnore
    Long id;

    @SearchIndex
    @NotNull
    @Size(min = 1)
    String handle;

    public String getHandle()
    {
        return handle;
    }

    public void setHandle(String handle)
    {
        this.handle = handle;
    }
    
    ////////////////////////////////////////////////
    
    public void fromProduct(Product p)
    {
        this.setHandle(p.getHandle());
        //this.setTenant(p.getTenant());
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
         
       return   //com.google.common.base.Objects.equal(this.tenant, other.tenant)  
             com.google.common.base.Objects.equal(this.handle, other.handle);  
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
                 //.addValue(this.tenant)  
                 .addValue(this.handle)    
                 .toString();  
    }  
    
}
