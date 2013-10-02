package org.mayocat.shop.catalog.api.representations;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;
import org.mayocat.rest.representations.ImageRepresentation;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.meta.CollectionEntity;
import org.mayocat.rest.representations.EntityReferenceRepresentation;
import org.mayocat.rest.Resource;

import com.fasterxml.jackson.annotation.JsonInclude;

public class CollectionRepresentation
{
    @NotEmpty
    private String title;

    private String description;

    private String slug;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long numberOfProducts = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<EntityReferenceRepresentation> products = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ImageRepresentation featuredImage = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ImageRepresentation> images = null;

    private String href;

    public CollectionRepresentation()
    {
        // No-arg constructor required for Jackson de-serialization
    }

    public CollectionRepresentation(Collection collection)
    {
        this(collection, null);
    }

    public CollectionRepresentation(Long numberOfProducts, Collection collection)
    {
        this(collection, null);
        this.numberOfProducts = numberOfProducts;
    }

    public CollectionRepresentation(Collection collection, List<EntityReferenceRepresentation> products)
    {
        this.title = collection.getTitle();
        this.description = collection.getDescription();
        this.slug = collection.getSlug();

        this.href = Resource.API_ROOT_PATH + CollectionEntity.PATH + "/" + this.slug;

        this.products = products;
        if (products != null) {
            // FIXME
            // This assumes that ALL products for a collection gets passed.
            // We might want that if we decide that the product is not designed to handle thousands of products
            // per collection.
            // There is the (future, potential) case of "marketplace collections" to consider though,
            // where a single collection is shared across tenant and can have a large number of products.
            this.numberOfProducts = Long.valueOf(products.size());
        }
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

    public List<EntityReferenceRepresentation> getProducts()
    {
        return products;
    }

    public Long getNumberOfProducts()
    {
        return numberOfProducts;
    }

    public ImageRepresentation getFeaturedImage()
    {
        return featuredImage;
    }

    public void setFeaturedImage(ImageRepresentation featuredImage)
    {
        this.featuredImage = featuredImage;
    }

    public List<ImageRepresentation> getImages()
    {
        return images;
    }

    public void setImages(List<ImageRepresentation> images)
    {
        this.images = images;
    }
}
