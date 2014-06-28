/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mayocat.model.AddonGroup;
import org.mayocat.model.Association;
import org.mayocat.model.Child;
import org.mayocat.model.Entity;
import org.mayocat.model.HasAddons;
import org.mayocat.model.HasFeaturedImage;
import org.mayocat.model.HasModel;
import org.mayocat.model.HasType;
import org.mayocat.model.Localized;
import org.mayocat.model.annotation.DoNotIndex;
import org.mayocat.model.annotation.Index;
import org.mayocat.model.annotation.LocalizedField;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

@Index
public class Product implements Entity, HasAddons, HasModel, HasFeaturedImage, Purchasable, Localized, HasType, Child
{
    private static final long serialVersionUID = 6998229869430511994L;

    @DoNotIndex
    private UUID id;

    @DoNotIndex
    private UUID parentId = null;

    private Boolean onShelf;

    @NotNull
    @Size(min = 1)
    private String slug;

    @LocalizedField
    @NotNull
    @Size(min = 1)
    private String title;

    @LocalizedField
    private transient String description;

    private BigDecimal price;

    private BigDecimal weight;

    private Integer stock;

    @DoNotIndex
    private UUID featuredImageId;

    private Association<Map<String, AddonGroup>> addons = Association.notLoaded();

    private Association<Collection> featuredCollection = Association.notLoaded();

    private Association<List<Collection>> collections = Association.notLoaded();

    private Map<Locale, Map<String, Object>> localizedVersions;

    @DoNotIndex
    private Optional<String> model = Optional.absent();

    @DoNotIndex
    private Optional<String> type = Optional.absent();

    @DoNotIndex
    private List<UUID> features;

    @DoNotIndex
    private boolean virtual = false;

    @DoNotIndex
    private Optional<Association<Purchasable>> parent = Optional.absent();

    public Product()
    {
    }

    public Product(UUID id)
    {
        this.id = id;
    }

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public UUID getId()
    {
        return this.id;
    }

    public void setId(UUID id)
    {
        this.id = id;
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

    public BigDecimal getUnitPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public Optional<BigDecimal> getActualUnitPrice()
    {
        if (this.price != null) {
            return Optional.of(price);
        } else if (this.getParent().isPresent() && this.getParent().get().isLoaded()) {
            Purchasable parent = this.getParent().get().get();
            if (!Product.class.isAssignableFrom(parent.getClass())) {
                throw new RuntimeException("Cannot handle a parent purchasable that is not a product");
            }
            Product parentProduct = (Product) parent;
            return Optional.fromNullable(parentProduct.getPrice());
        }
        return Optional.absent();
    }

    public BigDecimal getWeight()
    {
        return weight;
    }

    public void setWeight(BigDecimal weight)
    {
        this.weight = weight;
    }

    public Optional<BigDecimal> getActualWeight()
    {
        if (this.weight != null) {
            return Optional.of(weight);
        } else if (this.getParent().isPresent() && this.getParent().get().isLoaded()) {
            Purchasable parent = this.getParent().get().get();
            if (!Product.class.isAssignableFrom(parent.getClass())) {
                throw new RuntimeException("Cannot handle a parent purchasable that is not a product");
            }
            Product parentProduct = (Product) parent;
            return Optional.fromNullable(parentProduct.getWeight());
        }
        return Optional.absent();
    }

    @Override
    public Association<Map<String, AddonGroup>> getAddons()
    {
        return addons;
    }

    @Override
    public void setAddons(Map<String, AddonGroup> addons)
    {
        this.addons = new Association(addons);
    }

    public Association<Collection> getFeaturedCollection()
    {
        return featuredCollection;
    }

    public void setFeaturedCollection(Collection featuredCollection)
    {
        this.featuredCollection = new Association<>(featuredCollection);
    }

    public Association<List<Collection>> getCollections()
    {
        return collections;
    }

    public void setCollections(List<Collection> collections)
    {
        this.collections = new Association<>(collections);
    }

    public void setModel(String model)
    {
        this.model = Optional.fromNullable(model);
    }

    public Optional<String> getModel()
    {
        return model;
    }

    public UUID getFeaturedImageId()
    {
        return this.featuredImageId;
    }

    public void setFeaturedImageId(UUID featuredImageId)
    {
        this.featuredImageId = featuredImageId;
    }

    public Integer getStock()
    {
        return stock;
    }

    public void setStock(Integer stock)
    {
        this.stock = stock;
    }

    public void setLocalizedVersions(Map<Locale, Map<String, Object>> versions)
    {
        this.localizedVersions = versions;
    }

    public Map<Locale, Map<String, Object>> getLocalizedVersions()
    {
        return localizedVersions;
    }

    public Optional<String> getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = Optional.fromNullable(type);
    }

    public List<UUID> getFeatures()
    {
        return features;
    }

    public void setFeatures(List<UUID> features)
    {
        this.features = features;
    }

    public boolean isVirtual()
    {
        return virtual;
    }

    public void setVirtual(boolean virtual)
    {
        this.virtual = virtual;
    }

    public UUID getParentId()
    {
        return parentId;
    }

    public void setParentId(UUID parentId)
    {
        this.parentId = parentId;
        if (parentId != null) {
            Association<Purchasable> notLoaded = Association.notLoaded();
            this.parent = Optional.of(notLoaded);
        }
    }

    public void setParent(@Nullable Purchasable purchasable)
    {
        if (purchasable != null) {
            parent = Optional.of(new Association<>(purchasable));
        }
    }

    @Override
    public Optional<Association<Purchasable>> getParent()
    {
        return this.parent;
    }

    ////////////////////////////////////////////////

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Product other = (Product) obj;

        return Objects.equal(this.id, other.id)
                && Objects.equal(this.title, other.title)
                && Objects.equal(this.slug, other.slug)
                && Objects.equal(this.onShelf, other.onShelf)
                && Objects.equal(this.price, other.price)
                && Objects.equal(this.stock, other.stock)
                && Objects.equal(this.weight, other.weight)
                && Objects.equal(this.featuredImageId, other.featuredImageId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(
                this.slug,
                this.title,
                this.onShelf,
                this.price,
                this.stock,
                this.featuredImageId,
                this.weight
        );
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .addValue(id)
                .addValue(this.title)
                .addValue(this.slug)
                .addValue(this.onShelf)
                .toString();
    }
}
