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
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.hibernate.validator.constraints.NotEmpty
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.mayocat.accounts.api.v1.object.TenantApiObject
import org.mayocat.accounts.model.Tenant
import org.mayocat.configuration.PlatformSettings
import org.mayocat.context.request.WebRequest
import org.mayocat.image.model.Image
import org.mayocat.model.AddonGroup
import org.mayocat.rest.api.object.AddonGroupApiObject
import org.mayocat.rest.api.object.BaseApiObject
import org.mayocat.rest.api.object.ImageApiObject
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.taxes.configuration.Mode
import org.mayocat.shop.taxes.configuration.Rate
import org.mayocat.shop.taxes.configuration.TaxesSettings
import org.mayocat.theme.ThemeDefinition

import java.math.RoundingMode

import static org.mayocat.rest.api.object.AddonGroupApiObject.forAddonGroup
import static org.mayocat.rest.api.object.AddonGroupApiObject.toAddonGroupMap

/**
 * API object for product APIs
 *
 * See {@link org.mayocat.shop.catalog.api.v1.TenantProductApi}
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

    @JsonIgnore
    // Ignored on de-serialization only. See getter and setter
    DateTime creationDate;

    @JsonProperty("creationDate")
    public DateTime getCreationDate() {
        return creationDate;
    }

    @JsonIgnore
    public void setCreationDate(DateTime creationDate)
    {
        this.creationDate = creationDate;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    BigDecimal price;

    Optional<String> vatRate = Optional.absent()

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
    ProductApiObject withProduct(TaxesSettings taxesSettings, Product product, Optional<Product> parent)
    {

        slug = product.slug
        title = product.title
        description = product.description
        onShelf = product.onShelf
        weight = product.weight
        stock = product.stock

        type = product.type.orNull()
        model = product.model.orNull()

        _localized = product.localizedVersions

        vatRate = product.vatRateId

        if (product.creationDate != null) {
            def timeZone = DateTimeZone.default //FIXME
            creationDate = new DateTime(product.creationDate.time, timeZone);
        }

        if (product.price) {
            this.price = product.price
            if (taxesSettings.mode.value.equals(Mode.INCLUSIVE_OF_TAXES)) {
                BigDecimal vatRate
                if (product.vatRateId.isPresent()) {
                    vatRate = taxesSettings.vat.value.otherRates
                            .find({ Rate rate -> rate.id == product.vatRateId.get() })?.value;
                }
                if (!vatRate && parent.isPresent() && parent.get().vatRateId.isPresent()) {
                    vatRate = taxesSettings.vat.value.otherRates
                            .find({ Rate rate -> rate.id == parent.get().vatRateId.get() })?.value;
                }
                if (!vatRate) {
                    vatRate = taxesSettings.vat.value.defaultRate
                }
                this.price = BigDecimal.ONE.add(vatRate).multiply(product.price)
            }
        }

        this
    }

    @JsonIgnore
    Product toProduct(TaxesSettings taxesSettings, PlatformSettings platformSettings, Optional<ThemeDefinition> themeDefinition,
            Optional<Product> parent = Optional.absent())
    {
        def product = new Product()
        product.with {
            title = this.title
            description = this.description
            onShelf = this.onShelf
            weight = this.weight
            stock = this.stock

            setModel this.model
            setType this.type

            setLocalizedVersions this._localized
        }

        if (vatRate.isPresent()) {
            product.setVatRateId(vatRate.get())
        }

        if (this.price) {
            product.price = this.price

            if (taxesSettings.mode.value.equals(Mode.INCLUSIVE_OF_TAXES)) {

                // If taxes are managed inclusively (API consumer sends inclusive prices), we compute the exclusive
                // price, which is the price we manipulate and store internally.

                BigDecimal vatRate
                if (product.vatRateId.isPresent()) {
                    vatRate = taxesSettings.vat.value.otherRates
                            .find({ Rate rate -> rate.id == product.vatRateId.get() })?.value;
                    if (!vatRate) {
                        // VAT rate defined in product, but does not exists in configuration -> change to default rate
                        product.setVatRateId(null);
                    }
                }
                else if (parent.isPresent() && parent.get().vatRateId.isPresent()) {
                    // For a variant, if the VAT rate is not defined but the parent product is, we use it
                    vatRate = taxesSettings.vat.value.otherRates
                            .find({ Rate rate -> rate.id == parent.get().vatRateId.get() })?.value;
                }
                if (!vatRate) {
                    vatRate = taxesSettings.vat.value.defaultRate
                }
                if (!vatRate.equals(BigDecimal.ZERO)) {
                    BigDecimal conversionRate = BigDecimal.ONE.
                            divide(BigDecimal.ONE.add(vatRate), 10, RoundingMode.HALF_UP)
                    product.price = this.price.multiply(conversionRate)
                }
            }
        }


        if (addons) {
            product.addons = toAddonGroupMap(addons, platformSettings, themeDefinition)
        }

        if (parent.isPresent()) {
            product.setParent(parent.get())
            product.setParentId(parent.get().getId())
        }

        product
    }

    @JsonIgnore
    ProductApiObject withCollectionRelationships(List<org.mayocat.shop.catalog.model.Collection> collections)
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

        this
    }

    @JsonIgnore
    ProductApiObject withEmbeddedVariants(TaxesSettings settings, List<Product> variants, Product product, WebRequest webRequest)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        List<ProductApiObject> variantApiObjects = []

        variants.each({ Product variant ->
            ProductApiObject object = new ProductApiObject([
                    _href: "${webRequest.tenantPrefix}/api/products/${this.slug}/variants/${variant.slug}"
            ])
            object.withProduct(settings, variant, Optional.of(product))
            if (variant.getAddons().isLoaded()) {
                object.withAddons(variant.getAddons().get())
            }
            variantApiObjects << object
        })

        _embedded.variants = variantApiObjects

        this
    }

    @JsonIgnore
    ProductApiObject withEmbeddedImages(List<Image> images, UUID featuredImageId, String tenantPrefix)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        ImageApiObject featuredImage

        def List<ImageApiObject> imageApiObjectList = [];

        images.each({ Image image ->

            ImageApiObject imageApiObject = new ImageApiObject()
            imageApiObject.withImage(image, tenantPrefix)
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

        this
    }

    @JsonIgnore
    ProductApiObject withEmbeddedFeaturedImage(Image featuredImage, String tenantPrefix)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        def imageApiObject = new ImageApiObject()
        imageApiObject.withImage(featuredImage, tenantPrefix)
        imageApiObject.featured = true
        _embedded.featuredImage = imageApiObject

        this
    }

    @JsonIgnore
    ProductApiObject withAddons(Map<String, AddonGroup> entityAddons) {
        if (!addons) {
            addons = [:]
        }

        entityAddons.values().each({ AddonGroup addon ->
            addons.put(addon.group, forAddonGroup(addon))
        })

        this
    }

    @JsonIgnore
    ProductApiObject withEmbeddedTenant(Tenant tenant, DateTimeZone timeZone)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        TenantApiObject tenantApiObject = new TenantApiObject().withTenant(tenant, timeZone)

        _embedded.tenant = tenantApiObject

        this
    }
}
