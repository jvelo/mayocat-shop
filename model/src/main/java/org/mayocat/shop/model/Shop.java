package org.mayocat.shop.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Shop implements Entity
{
    @JsonIgnore
    Long id;

    String name;

    List<Product> products;

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
            return;
        }
        this.products.remove(product);
    }

    public List<Product> getProducts()
    {
        return this.products;
    }
    
    public void setProducts(List<Product> products)
    {
        this.products = products;
    }
}
