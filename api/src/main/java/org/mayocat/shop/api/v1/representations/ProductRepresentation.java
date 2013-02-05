package org.mayocat.shop.api.v1.representations;

import java.util.List;

import org.mayocat.shop.model.Product;
import org.mayocat.shop.rest.representations.EntityReferenceRepresentation;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ProductRepresentation
{
    private String slug;

    private String title;

    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<EntityReferenceRepresentation> categories = null;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String href;

    public ProductRepresentation(Product product)
    {
        this(product, null);
    }

    public ProductRepresentation(Product product, List<EntityReferenceRepresentation> categories)
    {
        this.slug = product.getSlug();
        this.title = product.getTitle();
        this.description = product.getDescription();

        this.href = "/product/" + this.slug;

        this.categories = categories;
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

    public List<EntityReferenceRepresentation> getCategories()
    {
        return categories;
    }
}
