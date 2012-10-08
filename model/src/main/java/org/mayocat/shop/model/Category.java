package org.mayocat.shop.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.mayocat.shop.model.annotation.Localizable;
import org.mayocat.shop.model.annotation.SearchIndex;

import com.google.common.base.Objects;

public class Category implements HandleableEntity
{
    @JsonIgnore
    Long id;

    @SearchIndex
    @NotNull
    @Size(min = 1)
    String handle;

    @Localizable
    @SearchIndex
    @NotNull
    String title;

    @Localizable
    @SearchIndex
    String description;

    boolean special = false;

    List<Product> products;

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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean isSpecial()
    {
        return special;
    }

    public void setSpecial(boolean special)
    {
        this.special = special;
    }

    public List<Product> getProducts()
    {
        return products;
    }

    public void setProducts(List<Product> products)
    {
        this.products = products;
    }

    public void addToProducts(Product product)
    {
        if (this.products == null) {
            this.products = new ArrayList<Product>();
        }
        this.products.add(product);
    }

    public void removeFromProducts(Product product)
    {
        if (this.products == null) {
            this.products = new ArrayList<Product>();
        }
        this.products.remove(product);
    }

    // //////////////////////////////////////////////

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Category other = (Category) obj;

        return Objects.equal(this.title, other.title) && Objects.equal(this.handle, other.handle)
            && Objects.equal(this.special, other.special) && Objects.equal(this.description, other.description);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.handle, this.title, this.special, this.description);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).addValue(this.title).addValue(this.handle).addValue(this.special)
            .toString();
    }

}
