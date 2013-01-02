package org.mayocat.shop.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.shop.model.annotation.Localizable;
import org.mayocat.shop.model.annotation.SearchIndex;

import com.google.common.base.Objects;

public class Product implements EntityWithSlug
{
    Long id;

    @SearchIndex
    @NotNull
    @Size(min = 1)
    String slug;
    
    @Localizable
    @SearchIndex
    @NotNull
    @Size(min = 1)
    String title;

    @Localizable
    @SearchIndex
    String description;
    
    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
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
         
       return   Objects.equal(this.title, other.title)  
             && Objects.equal(this.slug, other.slug);  
    }  
    
    @Override
    public int hashCode()  
    {  
        return Objects.hashCode(this.slug, this.title);  
    }
    
    @Override  
    public String toString()  
    {  
       return Objects.toStringHelper(this)  
                 .addValue(this.title)  
                 .addValue(this.slug)    
                 .toString();  
    }  
    
}
