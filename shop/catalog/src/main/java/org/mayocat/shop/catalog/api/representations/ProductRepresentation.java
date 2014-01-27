/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.api.representations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;
import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.rest.representations.ImageRepresentation;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.meta.ProductEntity;
import org.mayocat.rest.representations.EntityReferenceRepresentation;
import org.mayocat.rest.Resource;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ProductRepresentation
{
    private String slug;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String model;

    @NotEmpty
    private String title;

    private String description;

    private Boolean onShelf;

    private BigDecimal price;

    private BigDecimal weight;

    private Integer stock;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ImageRepresentation featuredImage = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<EntityReferenceRepresentation> collections = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ImageRepresentation> images = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AddonRepresentation> addons = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Locale, Map<String, Object>> localizedVersions = null;

    private String href;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ProductRepresentation()
    {
        // No-arg constructor required for Jackson de-serialization
    }

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
        this.model = product.getModel().orNull();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.onShelf = product.getOnShelf();
        this.price = product.getUnitPrice();
        this.weight = product.getWeight();
        this.stock = product.getStock();
        this.localizedVersions = product.getLocalizedVersions();
        this.type = product.getType().orNull();

        this.href = Resource.API_ROOT_PATH + ProductEntity.PATH + "/" + this.slug;

        this.collections = collections;
        this.images = images;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    public BigDecimal getWeight()
    {
        return weight;
    }

    public void setWeight(BigDecimal weight)
    {
        this.weight = weight;
    }

    public List<AddonRepresentation> getAddons()
    {
        return addons;
    }

    public void setAddons(List<AddonRepresentation> addons)
    {
        this.addons = addons;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Integer getStock()
    {
        return stock;
    }

    public void setStock(Integer stock)
    {
        this.stock = stock;
    }

    public ImageRepresentation getFeaturedImage()
    {
        return featuredImage;
    }

    public void setFeaturedImage(ImageRepresentation featuredImage)
    {
        this.featuredImage = featuredImage;
    }

    public Map<Locale, Map<String, Object>> getLocalizedVersions()
    {
        return localizedVersions;
    }

}
