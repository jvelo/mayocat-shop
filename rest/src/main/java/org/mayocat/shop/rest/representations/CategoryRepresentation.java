package org.mayocat.shop.rest.representations;

import org.mayocat.shop.model.Category;

public class CategoryRepresentation
{
    private String title;

    private String description;

    private String slug;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String href;

    public CategoryRepresentation(Category category)
    {
        this.title = category.getTitle();
        this.description = category.getDescription();
        this.slug = category.getSlug();

        this.href = "/category/" + this.slug;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getSlug()
    {
        return slug;
    }

    public String getHref()
    {
        return href;
    }
}
