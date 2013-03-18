package org.mayocat.shop.catalog.api.representations;

import java.math.BigDecimal;
import java.util.List;

import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.rest.representations.ImageRepresentation;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.rest.representations.EntityReferenceRepresentation;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ProductRepresentation
{
    private String slug;

    private String title;

    private String description;

    private Boolean onShelf;

    private BigDecimal price;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<EntityReferenceRepresentation> collections = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ImageRepresentation> images = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AddonRepresentation> addons = null;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String href;

    public ProductRepresentation(Product product)
    {
        this(product, null, null);
    }

    public ProductRepresentation(Product product, List<EntityReferenceRepresentation> collections)
    {
        this(product, collections, null);
    }

    public ProductRepresentation(Product product, List<EntityReferenceRepresentation> collections,
            List<ImageRepresentation> images)
    {
        this.slug = product.getSlug();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.onShelf = product.getOnShelf();
        this.price = product.getPrice();

        this.href = "/api/1.0/product/" + this.slug;

        this.collections = collections;
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

    public List<EntityReferenceRepresentation> getCollections()
    {
        return collections;
    }

    public void setCollections(List<EntityReferenceRepresentation> collections)
    {
        this.collections = collections;
    }

    public List<ImageRepresentation> getImages()
    {
        return images;
    }

    public void setImages(List<ImageRepresentation> images)
    {
        this.images = images;
    }


    public Boolean getOnShelf()
    {
        return onShelf;
    }

    public void setOnShelf(Boolean onShelf)
    {
        this.onShelf = onShelf;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public List<AddonRepresentation> getAddons()
    {
        return addons;
    }

    public void setAddons(List<AddonRepresentation> addons)
    {
        this.addons = addons;
    }
}
