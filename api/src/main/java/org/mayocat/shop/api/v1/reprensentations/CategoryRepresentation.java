package org.mayocat.shop.api.v1.reprensentations;

import java.util.List;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.rest.representations.EntityReferenceRepresentation;

import com.fasterxml.jackson.annotation.JsonInclude;

public class CategoryRepresentation
{
    private String title;

    private String description;

    private String slug;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long numberOfProducts = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<EntityReferenceRepresentation> products = null;

    private String href;

    public CategoryRepresentation(Category category)
    {
        this(category, null);
    }

    public CategoryRepresentation(Long numberOfProducts, Category category)
    {
        this(category, null);
        this.numberOfProducts = numberOfProducts;
    }

    public CategoryRepresentation(Category category, List<EntityReferenceRepresentation> products)
    {
        this.title = category.getTitle();
        this.description = category.getDescription();
        this.slug = category.getSlug();

        this.href = "/category/" + this.slug;

        this.products = products;
        if (products != null) {
            // FIXME
            // This assumes that ALL products for a category gets passed.
            // We might want that if we decide that the product is not designed to handle thousands of products
            // per category.
            // There is the (future, potential) case of "marketplace categories" to consider though,
            // where a single category is shared across tenant and can have a large number of products.
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
