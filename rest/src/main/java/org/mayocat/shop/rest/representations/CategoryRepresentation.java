package org.mayocat.shop.rest.representations;

import org.mayocat.shop.model.Category;

@SuppressWarnings("unused")
public class CategoryRepresentation
{
    private String title;

    private String description;

    private String handle;

    private boolean special;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String href;

    public CategoryRepresentation(Category category)
    {
        this.title = category.getTitle();
        this.description = category.getDescription();
        this.handle = category.getHandle();
        this.special = category.isSpecial();

        this.href = "/category/" + this.handle;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public String getHandle()
    {
        return handle;
    }
    
    public String getHref()
    {
        return href;
    }
}
