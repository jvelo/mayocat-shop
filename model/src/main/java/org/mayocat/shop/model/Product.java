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
    
    @SearchIndex
    @NotNull
    String title;

    public String getHandle()
    {
        return handle;
    }

    public void setHandle(String handle)
    {
        this.handle = handle;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
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
         
       return   com.google.common.base.Objects.equal(this.title, other.title)  
             && com.google.common.base.Objects.equal(this.handle, other.handle);  
    }  
    
    @Override
    public int hashCode()  
    {  
        return com.google.common.base.Objects.hashCode(this.handle, this.title);  
    }
    
    @Override  
    public String toString()  
    {  
       return com.google.common.base.Objects.toStringHelper(this)  
                 .addValue(this.title)  
                 .addValue(this.handle)    
                 .toString();  
    }  
    
}
