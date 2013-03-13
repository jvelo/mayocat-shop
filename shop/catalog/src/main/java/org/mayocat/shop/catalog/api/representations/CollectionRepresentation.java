package org.mayocat.shop.catalog.api.representations;

import java.util.List;

import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.rest.representations.EntityReferenceRepresentation;

import com.fasterxml.jackson.annotation.JsonInclude;

public class CollectionRepresentation
{
    private String title;

    private String description;

    private String slug;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long numberOfProducts = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<EntityReferenceRepresentation> products = null;

    private String href;

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

        this.href = "/collection/" + this.slug;

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
}
