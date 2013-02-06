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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AttachmentRepresentation> images = null;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String href;

    public ProductRepresentation(Product product)
    {
        this(product, null, null);
    }

    public ProductRepresentation(Product product, List<EntityReferenceRepresentation> categories)
    {
        this(product, categories, null);
    }

    public ProductRepresentation(Product product, List<EntityReferenceRepresentation> categories,
            List<AttachmentRepresentation> images)
    {
        this.slug = product.getSlug();
        this.title = product.getTitle();
        this.description = product.getDescription();

        this.href = "/product/" + this.slug;

        this.categories = categories;
        this.images = images;
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

    public void setCategories(List<EntityReferenceRepresentation> categories)
    {
        this.categories = categories;
    }

    public List<AttachmentRepresentation> getImages()
    {
        return images;
    }

    public void setImages(List<AttachmentRepresentation> images)
    {
        this.images = images;
    }
}
