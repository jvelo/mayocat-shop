package org.mayocat.shop.rest.representations;

import org.mayocat.shop.model.Product;

public class ProductRepresentation
{

    private String slug;

    private String title;

    private String description;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String href;

    public ProductRepresentation(Product product)
    {
        this.slug = product.getSlug();
        this.title = product.getTitle();
        this.description = product.getDescription();

        this.href = "/product/" + this.slug;
    }
    
    public String getSlug()
    {
        return slug;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public String getHref()
    {
        return href;
    }


}
