package org.mayocat.shop.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.shop.model.annotation.Localized;
import org.mayocat.shop.model.annotation.SearchIndex;
import org.mayocat.shop.model.reference.EntityReference;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

public class Product extends AbstractLocalizedEntity
{
    private Long id;

    @SearchIndex
    private boolean onShelf = false;

    @SearchIndex
    @NotNull
    @Size(min = 1)
    private String slug;
    
    @Localized
    @SearchIndex
    @NotNull
    @Size(min = 1)
    private String title;

    @Localized
    @SearchIndex
    private String description;

    public Product()
    {
    }

    public Product(Long id)
    {
        this.id = id;
    }

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
    
    public Long getId()
    {
        return this.id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public boolean isOnShelf()
    {
        return onShelf;
    }

    public void setOnShelf(boolean onShelf)
    {
        this.onShelf = onShelf;
    }

    @Override
    public EntityReference getReference()
    {
        return new EntityReference("product", getSlug(), Optional.<EntityReference>absent());
    }

    @Override
    public EntityReference getParentReference()
    {
        return null;
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
