package org.mayocat.shop.catalog.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.model.AbstractLocalizedEntity;
import org.mayocat.model.annotation.Localized;
import org.mayocat.model.annotation.SearchIndex;
import org.mayocat.model.reference.EntityReference;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

public class Product extends AbstractLocalizedEntity
{
    private Long id;

    @SearchIndex
    private Boolean onShelf;

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

    @SearchIndex
    private BigDecimal price;

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

    public Boolean getOnShelf()
    {
        return onShelf;
    }

    public void setOnShelf(Boolean onShelf)
    {
        this.onShelf = onShelf;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    @Override
    public EntityReference getReference()
    {
        return new EntityReference("product", getSlug(), Optional.<EntityReference>absent());
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
             && Objects.equal(this.slug, other.slug)
             && Objects.equal(this.description, other.description)
             && Objects.equal(this.onShelf, other.onShelf)
             && Objects.equal(this.price, other.price);
    }  
    
    @Override
    public int hashCode()  
    {  
        return Objects.hashCode(this.slug, this.title, this.description, this.onShelf, this.price);
    }
    
    @Override  
    public String toString()  
    {  
       return Objects.toStringHelper(this)  
                 .addValue(this.title)  
                 .addValue(this.slug)
                 .addValue(this.onShelf)
                 .addValue(this.price)
                 .toString();  
    }
}
