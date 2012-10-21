package org.mayocat.shop.rest.representations;

import org.mayocat.shop.model.Product;

@SuppressWarnings("unused")
public class ProductRepresentation
{

    private String handle;

    private String title;

    private String description;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String href;

    public ProductRepresentation(Product product)
    {
        this.handle = product.getHandle();
        this.title = product.getTitle();
        this.description = product.getDescription();

        this.href = "/product/" + this.handle;
    }
    
    public String getHandle()
    {
        return handle;
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
