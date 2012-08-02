package org.mayocat.shop.model;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.Extensions;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@PersistenceCapable(
    table = "product",
    detachable="true"
)
@Extensions({
    @Extension(vendorName="datanucleus", key="multitenancy-column-name", value="tenant"),
    @Extension(vendorName="datanucleus", key="multitenancy-column-length", value="255")
})
public class Product extends Entity
{
    @Index
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    // Fix behavior in mysql : http://www.datanucleus.org/servlet/forum/viewthread_thread,6289
    // FIXME in the future ORM mapping will be taken out of the model classes to XML files in the datanucleus module
    // (for flexibility and separation for concern reasons) ; thus we will be able to isolate this fix just for mysql.
    @Column(length = 255)
    private Long id;

    @Index
    @NotNull
    @Unique
    @Size(min = 1)
    // Same as above
    @Column(length = 255)
    private String handle;
        
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
