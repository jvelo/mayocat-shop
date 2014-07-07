/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.api.v1.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.hibernate.validator.constraints.NotEmpty
import org.mayocat.configuration.PlatformSettings
import org.mayocat.image.model.Image
import org.mayocat.model.AddonGroup
import org.mayocat.rest.api.object.AddonGroupApiObject
import org.mayocat.rest.api.object.BaseApiObject
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.shop.catalog.model.Product
import org.mayocat.theme.ThemeDefinition

import static org.mayocat.rest.api.object.AddonGroupApiObject.forAddonGroup
import static org.mayocat.rest.api.object.AddonGroupApiObject.toAddonGroupMap

/**
 * API object for product APIs
 *
 * See {@link org.mayocat.shop.catalog.api.v1.ProductApi}
 *
 * @version $Id$
 */
@CompileStatic
class ProductApiObject extends BaseApiObject
{
    String slug;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String model;

    @NotEmpty
    String title;

    String description;

    Boolean onShelf;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    BigDecimal price;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    BigDecimal weight;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer stock;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, AddonGroupApiObject> addons

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> _embedded

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> _relationships;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<Locale, Map<String, Object>> _localized;

    // Helper builder methods

    @JsonIgnore
    def withProduct(Product product)
    {
        this.with {
            slug = product.slug
            title = product.title
            description = product.description
            onShelf = product.onShelf
            price = product.price
            weight = product.weight
            stock = product.stock

            type = product.type.orNull()
            model = product.model.orNull()

            _localized = product.localizedVersions
        }
    }

    @JsonIgnore
    Product toProduct(PlatformSettings platformSettings, Optional<ThemeDefinition> themeDefinition,
            Optional<Product> parent = Optional.absent())
    {
        def product = new Product()
        product.with {
            title = this.title
            description = this.description
            onShelf = this.onShelf
            price = this.price
            weight = this.weight
            stock = this.stock

            setModel this.model
            setType this.type

            setLocalizedVersions this._localized
        }

        if (addons) {
            product.addons = toAddonGroupMap(addons, platformSettings, themeDefinition)
        }

        product
    }

    @JsonIgnore
    def withCollectionRelationships(List<org.mayocat.shop.catalog.model.Collection> collections)
    {
        if (_relationships == null) {
            _relationships = [:]
        }

        List collectionRelationships = [];

        collections.each({ org.mayocat.shop.catalog.model.Collection collection ->
            def link = "/api/collections/${collection.slug}"
            collectionRelationships << [
                    title: collection.title,
                    slug: collection.slug,
                    _links: [self: [href: link]],
                    _href: link
            ]
        })

        _relationships.collections = collectionRelationships
    }

    @JsonIgnore
    def withEmbeddedVariants(List<Product> variants)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        List<ProductApiObject> variantApiObjects = []

        variants.each({ Product variant ->
            ProductApiObject object = new ProductApiObject([
                    _href: "/api/products/${this.slug}/variants/${variant.slug}"
            ])
            object.withProduct(variant)
            if (variant.getAddons().isLoaded()) {
                object.withAddons(variant.getAddons().get())
            }
            variantApiObjects << object
        })

        _embedded.variants = variantApiObjects
    }

    @JsonIgnore
    def withEmbeddedImages(List<Image> images, UUID featuredImageId)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        ImageApiObject featuredImage

        def List<ImageApiObject> imageApiObjectList = [];

        images.each({ Image image ->

            ImageApiObject imageApiObject = new ImageApiObject()
            imageApiObject.withImage(image)
            imageApiObject.featured = false

            if (image.attachment.id == featuredImageId) {
                featuredImage = imageApiObject
                imageApiObject.featured = true
            }
            imageApiObjectList << imageApiObject
        })

        _embedded.images = imageApiObjectList;

        if (featuredImage) {
            _embedded.featuredImage = featuredImage
        }
    }

    @JsonIgnore
    def withEmbeddedFeaturedImage(Image featuredImage)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        def imageApiObject = new ImageApiObject()
        imageApiObject.withImage(featuredImage)
        imageApiObject.featured = true
        _embedded.featuredImage = imageApiObject
    }

    @JsonIgnore
    def withAddons(Map<String, AddonGroup> entityAddons) {
        if (!addons) {
            addons = [:]
        }

        entityAddons.values().each({ AddonGroup addon ->
            addons.put(addon.group, forAddonGroup(addon))
        })
    }
}
